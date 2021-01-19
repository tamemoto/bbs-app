package com.example.app.bbs.app.service

import com.example.app.bbs.domain.entity.User
import com.example.app.bbs.domain.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.security.crypto.password.PasswordEncoder

@Service
class UserManagerServiceImpl: IUserDetailsService {
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    override fun registerUser(user: User, rawPassword: String) {
        user.password = passwordEncoder.encode(rawPassword)
        userRepository.save(user)
    }
}