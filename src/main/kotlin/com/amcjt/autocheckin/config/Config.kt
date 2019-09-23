package com.amcjt.autocheckin.config

import lombok.Data
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
@Data
class Config {
    @Value("\${autoCheck.oauth.url}")
    var oauthUrl: String = ""
    @Value("\${autoCheck.attendance.url}")
    var attendanceUrl: String = ""
}