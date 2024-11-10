package com.yupi.yuapigateway;

import com.example.yuapiclientsdk.utils.SignUtils;
import com.yupi.common.model.entity.InterfaceInfo;
import com.yupi.common.model.entity.User;
import com.yupi.common.services.InnerInterfaceInfoService;
import com.yupi.common.services.InnerUserInterfaceInfoService;
import com.yupi.common.services.InnerUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerUserService innerUserService;
    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;
    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1", "0:0:0:0:0:0:0:1");

    private static final String INTERFACE_HOST = "http://localhost:8123";
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.用户发送请求到API网关
        //2.请求日志
        ServerHttpRequest request = exchange.getRequest();
        String path = INTERFACE_HOST+request.getPath().value();
        String method = request.getMethod().toString();
        log.info("request id: {}", request.getId());
        log.info("request url: {}", request.getURI());
        log.info("request method: {}", method);
        log.info("request path: {}", path);
        String hostString = request.getLocalAddress().getHostString();
        log.info("request host: {}", hostString);
        //拿到响应对象
        ServerHttpResponse response = exchange.getResponse();
        //3.(黑白名单)
        if (!IP_WHITE_LIST.contains(hostString)) {
            //禁止访问
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }
        //4.鉴权(判断 accessKey,secretKey是否合法)
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String body = headers.getFirst("body");
        String sign = headers.getFirst("sign");
        String timestamp = headers.getFirst("timestamp");

        User invokeUser= null;
        try {
            //调用内部服务获取用户信息
            invokeUser=innerUserService.getInvokeUser(accessKey);
        } catch (Exception e) {
            log.error("getInvokeUser error"+e);
        }
        if(invokeUser==null){
            return handleNoAuth(response);
        }
        if(Long.parseLong(nonce)>10000L){
            return handleNoAuth(response);
        }
        //时间和当前时间不能超过5min
        Long currentTime = System.currentTimeMillis()/1000;
        Long startTime = Long.parseLong(timestamp);
        final Long FIVE_MINUTES = 60 * 5L;
        if(currentTime - startTime > FIVE_MINUTES){
            return handleNoAuth(response);
        }

        //5.判断签名是否合法
        String secretKey = invokeUser.getSecretKey();
        String serverSign = SignUtils.genSign(body, secretKey);
        if(!sign.equals(serverSign)){
            return handleNoAuth(response);
        }
        //5.请求的模拟接口是否存在
        InterfaceInfo interfaceInfo=null;
        try {
            interfaceInfo=innerInterfaceInfoService.getInterfaceInfo(path,method);
        } catch (Exception e) {
            log.error("getInterfaceInfo error"+e);
        }

        if(interfaceInfo==null){
            return handleNoAuth(response);
        }


        //6.请求转发，调用模拟接口
        //7.响应日志
        return handleResponse(exchange, chain,interfaceInfo.getId(),invokeUser.getId());

        //9.调用失败,返回一个错误码


    }

    /**
     * 处理响应
     *
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain,Long interfaceInfoId,Long userId) {
        try {
            // 获取原始的响应对象
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 获取数据缓冲工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 获取响应的状态码
            HttpStatus statusCode = originalResponse.getStatusCode();

            // 判断状态码是否为200 OK(按道理来说,现在没有调用,是拿不到响应码的,对这个保持怀疑 沉思.jpg)
            if(statusCode == HttpStatus.OK) {
                // 创建一个装饰后的响应对象(开始穿装备，增强能力)
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {

                    // 重写writeWith方法，用于处理响应体的数据
                    // 这段方法就是只要当我们的模拟接口调用完成之后,等它返回结果，
                    // 就会调用writeWith方法,我们就能根据响应结果做一些自己的处理
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        // 判断响应体是否是Flux类型
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 返回一个处理后的响应体
                            return super.writeWith(fluxBody.map(dataBuffer -> {
                                //调用次数+1
                                try {
                                    innerUserInterfaceInfoService.invokeCount(interfaceInfoId,userId);
                                } catch (Exception e) {
                                    log.error("invokeCount error"+e);
                                }
                                // 读取响应体的内容并转换为字节数组
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer);//释放掉内存
                                // 构建日志
                                StringBuilder sb2 = new StringBuilder(200);
                                List<Object> rspArgs = new ArrayList<>();
                                rspArgs.add(originalResponse.getStatusCode());
                                //rspArgs.add(requestUrl);
                                String data = new String(content, StandardCharsets.UTF_8);//data
                                sb2.append(data);
                                //打印日志
                                log.info("响应结果: {}", data);
                                //8.调用成功,接口调用次数 +1
                                // 将处理后的内容重新包装成DataBuffer并返回
                                return bufferFactory.wrap(content);
                            }));
                        } else {
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 对于200 OK的请求,将装饰后的响应对象传递给下一个过滤器链,并继续处理(设置repsonse对象为装饰过的)
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            // 对于非200 OK的请求，直接返回，进行降级处理
            return chain.filter(exchange);
        }catch (Exception e){
            // 处理异常情况，记录错误日志
            log.error("网关处理响应异常: " + e);
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }

    public Mono<Void> handleNoAuth(ServerHttpResponse response){
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }
}
