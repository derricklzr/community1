package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
@Deprecated
public interface LoginTicketMapper {
 /*
    对应login_ticket表，其中的String ticket为已登录凭证，不为空则登录了
    status为用户状态，0有效，1无效
    登录成功后，要把对应ticket传到浏览器cookie中
    之后浏览器可凭此成功凭证访问查到对应的login_ticket数据，服务端知道在线

    //用户登录成功后，给LoginTicket表插入完整数据
*/  @Insert({
         "insert into login_ticket(user_id,ticket,status,expired) ",
         "values(#{userId},#{ticket},#{status},#{expired}) "
 })
 @Options(useGeneratedKeys = true,keyProperty = "id")//这句意思是表中id随机生成
    int insertLoginTicket(LoginTicket loginTicket);


    //通过ticket查询具体login_ticket数据
    @Select({
            "select id,user_id,ticket,status,expired ",
            "from login_ticket where ticket=#{ticket} order by expired limit 0,1"
    })
    LoginTicket selectByTicker(String ticket);
    //输入ticket和状态参数，修改状态
    @Update({
            "<script>",
            "update login_ticket set status=#{status} where ticket=#{ticket}",
            "<if test=\"ticket!=null\">",
            "and 1=1",
            "</if>",
            "</script>",
    })
    int updateStatus(String ticket,int status);
}
