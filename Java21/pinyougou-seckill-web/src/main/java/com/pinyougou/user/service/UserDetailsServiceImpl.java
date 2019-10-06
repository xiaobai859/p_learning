package com.pinyougou.user.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @version V1.0
 * @typeName: UserDetailaServiceImpl
 * @classDesc: 认证类
 * @author：XiaoBai
 * @createTime：2019年8月10日 下午7:06:08
 * @remarks：
 */
public class UserDetailsServiceImpl implements UserDetailsService {


  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    System.out.println("经过认证类" + username);
    List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
    return new User(username, "", authorities);
  }

}
