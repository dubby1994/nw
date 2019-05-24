package cn.dubby.nw.util;

import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

/**
 * @author dubby
 * @date 2019/5/22 15:19
 */
public class IPUtil {

    public static final String getIP(ChannelHandlerContext ctx){
        InetSocketAddress ipSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        return ipSocket.getAddress().getHostAddress();
    }

}
