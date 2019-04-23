package com.shotacon.movie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shotacon.movie.model.ResMsg;
import com.shotacon.movie.service.AddressService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/v2/api/")
@Api(tags = { "地址管理" })
public class AddressController extends BaseController {

    @Autowired
    private AddressService addressService;

    @PostMapping("/address")
    @ApiOperation(value = "添加地址", notes = "添加地址", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg addAddress(@RequestHeader(name = "Authorization") String token,
            @RequestParam(value = "address", required = true) String address,
            @RequestParam(value = "isDefault", required = true) boolean isDefault,
            @RequestParam(value = "phone", required = true) String phone,
            @RequestParam(value = "name", required = true) String name) {
        return addressService.addAddress(token, address, phone, name, isDefault);
    }

    @DeleteMapping("/address/{id}")
    @ApiOperation(value = "删除地址", notes = "删除地址", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg deleteAddress(@RequestHeader(name = "Authorization") String token,
            @PathVariable(value = "id", required = true) int id) {
        return addressService.deleteAddress(token, id);
    }

    @PutMapping("/address/{id}")
    @ApiOperation(value = "修改地址", notes = "修改地址", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResMsg updateAddress(@RequestHeader(name = "Authorization") String token,
            @PathVariable(value = "id", required = true) int id,
            @RequestParam(value = "address", required = true) String address,
            @RequestParam(value = "phone", required = true) String phone,
            @RequestParam(value = "name", required = true) String name) {
        return addressService.updateAddress(token, address, phone, name, id);
    }

}
