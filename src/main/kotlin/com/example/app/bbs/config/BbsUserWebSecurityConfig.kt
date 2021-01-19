package com.example.app.bbs.config

import com.example.app.bbs.app.service.UserDetailsServiceImpl
import com.example.app.bbs.domain.entity.UserRole

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
@EnableWebSecurity
@Order(2)
//この()は継承元のコンストラクタを呼び出している
class BbsUserWebSecurityConfig: WebSecurityConfigurerAdapter() {
    @Autowired
    lateinit var userDetailsService: UserDetailsServiceImpl

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Override
    override fun configure(web: WebSecurity) {
        web.ignoring().antMatchers(
                "/favicon.ico",
                "/css/**",
                "/js/**"
        )
    }

    @Override
    override fun configure(http: HttpSecurity) {
        http
                // /user/以下に対して処理
                .antMatcher("/user/**")
                .authorizeRequests()
                // /user/loginと/user/signupは許可
                .antMatchers("/user/login").permitAll()
                .antMatchers("/user/signup").permitAll()
                // /user/**は一般ユーザー扱い
                .antMatchers("/user/**").hasRole(UserRole.USER.name)
                // 条件に一致するURLは認可が必要
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedPage("/user/login")
        http.formLogin()
                .loginProcessingUrl("/user/login/auth")
                .loginPage("/user/login")
                .failureForwardUrl("/user/login")
                .usernameParameter("email")
                .passwordParameter("password")
        http.logout()
                .logoutRequestMatcher(AntPathRequestMatcher("/logout**"))
                .logoutSuccessUrl("/")
    }

    @Override
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService)
    }

}