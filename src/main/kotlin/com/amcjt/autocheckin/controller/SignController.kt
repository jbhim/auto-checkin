package com.amcjt.autocheckin.controller

import com.amcjt.autocheckin.service.SignService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SignController(val signService: SignService) {
    @GetMapping("sign")
    fun sign(@RequestParam type: Int): Any? {
        val signType = if (type == 1) {
            SignService.SignType.SIGN_IN
        } else {
            SignService.SignType.SIGN_OUT
        }
        return signService.sign(signType)
    }

}