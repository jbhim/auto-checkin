package com.amcjt.autocheckin.service

import com.amcjt.autocheckin.config.Config
import lombok.extern.log4j.Log4j2
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Service
@Log4j2
class SignService(val restTemplate: RestTemplate,
                  val config: Config) {
    val tenantId = "16A58505-6D90-00B8-89AB-7E888323E42F"

    fun getToken(): String? {
        val param = LinkedMultiValueMap<String, String>()
        param["username"] = "zhengll "
        param["password"] = "zll0823"
        param["grant_type"] = "password"
        param["client_secret"] = "123456"
        param["client_id"] = "rc-client"
        param["scope"] = "read"
        param["response_type"] = "token"

        val httpHeaders = HttpHeaders()
        httpHeaders["Cookie"] = "tenantId=$tenantId"
        httpHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val httpEntity = HttpEntity<MultiValueMap<String, String>>(param, httpHeaders)
        var token: String? = null
        try {
            val map = restTemplate.postForEntity(config.oauthUrl, httpEntity, Map::class.java)
            token = map.body?.get("access_token").toString()
        } catch (e: Exception) {
            println("获取token失败")
            e.printStackTrace()
        }
        return token
    }

    data class SignEntity(
            var type: String? = null,
            var note: String? = null,
            var longitude: String? = null,
            var latitude: String? = null,
            var cid: String? = null,
            var imei: String? = null,
            var distance: String? = null,
            var longitudeStore: String? = null,
            var latitudeStore: String? = null,
            var address: String? = null
    )

    enum class SignType {
        SIGN_IN, SIGN_OUT
    }

    fun sign(signType: SignType): Any? {

        val signEntity = when (signType) {
            SignType.SIGN_IN -> SignEntity(type = "1",
                    longitude = "120.22284",
                    latitude = "30.20794",
                    cid = "cid_isjsjjskks",
                    imei = "354983074742197",
                    distance = "100",
                    longitudeStore = "120.22284",
                    latitudeStore = "30.20794",
                    address = "浙江省杭州市滨江区西兴街道智慧e谷A座智慧e谷大楼")

            SignType.SIGN_OUT -> SignEntity(type = "2",
                    longitude = "120.22284",
                    latitude = "30.20794",
                    cid = "cid_isjsjjskks",
                    imei = "354983074742197",
                    distance = "100",
                    longitudeStore = "120.22284",
                    latitudeStore = "30.20794",
                    address = "浙江省杭州市滨江区西兴街道智慧e谷A座智慧e谷大楼")

        }
        return sendSignRequest(signEntity)
    }

    private fun sendSignRequest(signEntity: SignEntity): MutableMap<String, Any> {
        val httpHeaders = HttpHeaders()
        val token = getToken()
        println("token = $token")
        httpHeaders["Authorization"] = "bearer $token"
        val httpEntity = HttpEntity(signEntity, httpHeaders)
        var result: MutableMap<*, *>? = null
        try {
            val map = restTemplate.postForEntity(config.attendanceUrl, httpEntity, MutableMap::class.java)
            result = map.body
        } catch (e: Exception) {
            println("获取自动打卡失败")
            e.printStackTrace()
        }
        return dealStatus(result)
    }

    fun dealStatus(map: MutableMap<*, *>?): MutableMap<String, Any> {

        val statusString = when (map?.get("status")) {
            "0000" -> "正常情况"
            "0001" -> "不需要考勤"
            "0002" -> "已经签到"
            "0004" -> "未设置考勤信息"
            "0007" -> "迟到早退"
            "0008" -> "不在考勤时间范围内"
            else -> "未知类型"
        }
        val mutableMap = map as MutableMap<String, Any>
        mutableMap["statusString"] = statusString
        return mutableMap
    }

}