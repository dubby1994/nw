package cn.dubby.nw.handler;

import cn.dubby.nw.util.IPUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.Set;

import static io.netty.handler.codec.http.HttpResponseStatus.FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author dubby
 * @date 2019/5/21 16:03
 */
public class AuthHandler extends ChannelInboundHandlerAdapter {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(AuthHandler.class);

    private static final String TOKEN_COOKIE_NAME = "t";

    private static final String loginPage = "/nn/login.html";

    private final String token;

    public AuthHandler(String token) {
        this.token = token;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest httpRequest = (FullHttpRequest) msg;
        if (loginPage.equalsIgnoreCase(httpRequest.uri())) {
            logger.info("login ip:{}", IPUtil.getIP(ctx));
            ctx.fireChannelRead(httpRequest);
            return;
        }

        String cookieString = httpRequest.headers().get(HttpHeaderNames.COOKIE);
        if (cookieString != null) {
            Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieString);
            if (!cookies.isEmpty()) {
                for (Cookie cookie : cookies) {
                    if (TOKEN_COOKIE_NAME.equalsIgnoreCase(cookie.name())) {
                        if (token.equals(cookie.value())) {
                            logger.info("pass ip:{}", IPUtil.getIP(ctx));
                            ctx.fireChannelRead(httpRequest);
                            return;
                        }
                    }
                }
            }
        }

        logger.info("reject ip:{}", IPUtil.getIP(ctx));
        needLogin(ctx);
    }

    private void needLogin(ChannelHandlerContext ctx) {
        FullHttpResponse needLoginResponse = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
        needLoginResponse.headers().set(HttpHeaderNames.LOCATION, loginPage);
        needLoginResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
        ChannelFuture flushPromise = ctx.writeAndFlush(needLoginResponse);
        flushPromise.addListener(ChannelFutureListener.CLOSE);
    }

}
