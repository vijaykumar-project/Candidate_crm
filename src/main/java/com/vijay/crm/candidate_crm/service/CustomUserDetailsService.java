package com.vijay.crm.candidate_crm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private JdbcTemplate jdbcTemplate; // inject JdbcTemplate

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try {
            UserDetails user = jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> org.springframework.security.core.userdetails.User.builder()
                        .username(rs.getString("username"))
                        .password(rs.getString("password"))
                        .roles(rs.getString("role"))
                        .build(),
                username
            );
            return user;
        } catch (EmptyResultDataAccessException e) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }

}
