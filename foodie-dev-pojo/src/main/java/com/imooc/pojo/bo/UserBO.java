package com.imooc.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "用户对象BO", description = "从客户端传入的信息封装在UserVO中")
public class UserBO {
    @ApiModelProperty(value = "用户名", name = "username", example = "imooc", required = true)
    private String username;
    @ApiModelProperty(value = "密码", name = "password", example = "abd333", required = true)
    private String password;
    @ApiModelProperty(value = "确认密码", name = "confirmPassword", example = "abd333", required = false)
    private String confirmPassword;
}
