package com.shotacon.movie.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shotacon.movie.config.ErrorCode;
import com.shotacon.movie.mapper.AddressMapper;
import com.shotacon.movie.mapper.UserMapper;
import com.shotacon.movie.model.ResMsg;

@Service
public class AddressService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    AddressMapper addressMapper;

    public ResMsg addAddress(String token, String address, String phone, String name, boolean isDefault) {
        List<Map<String, Object>> userList = userMapper.queryUserByToken(token);
        if (null == userList || userList.size() <= 0) {
            return ResMsg.fail(ErrorCode.TOKEN_ERROR, "Token非法或Token不存在");
        }
        int uid = Integer.valueOf(String.valueOf(userList.get(0).get("uid")));

        List<Map<String, Object>> addressList = addressMapper.getAllAddress(uid);
        // 如果没有地址, 新增并作为默认.
        if (null == addressList || addressList.size() <= 0) {
            addressMapper.addAddress(uid, address, phone, name, true);
        } else {
            if (isDefault) {
                // 如果有地址, 并勾选默认, 则新增设置为默认, 重置其他地址为非默认
                addressMapper.resetDefault(uid);
                addressMapper.addAddress(uid, address, phone, name, true);
            } else {
                // 未勾选默认, 则正常添加
                addressMapper.addAddress(uid, address, phone, name, false);
            }
        }
        return ResMsg.succ();
    }

    public ResMsg deleteAddress(String token, int id) {
        List<Map<String, Object>> userList = userMapper.queryUserByToken(token);
        if (null == userList || userList.size() <= 0) {
            return ResMsg.fail(ErrorCode.TOKEN_ERROR, "Token非法或Token不存在");
        }
        int uid = Integer.valueOf(String.valueOf(userList.get(0).get("uid")));
        List<Map<String, Object>> address = addressMapper.getAddress(uid, uid);
        if (null == address || address.size() <= 0) {
            return ResMsg.fail(ErrorCode.NO_ADDRESS_ERROR, "该账号下没有对应的地址");
        }
        // 移除
        addressMapper.removeAddress(id);
        // 如果移除的是默认地址
        if (Boolean.parseBoolean(String.valueOf(address.get(0).get("default_address")))) {
            // 查询剩余的地址
            List<Map<String, Object>> allAddress = addressMapper.getAllAddress(uid);
            if (null != allAddress && allAddress.size() > 0) {
                // 指定一个
                addressMapper.setDefault(Integer.parseInt(String.valueOf(allAddress.get(0).get("id"))));
            }
        }
        return ResMsg.succ();
    }

    public ResMsg updateAddress(String token, String address, String phone, String name, int id) {
        List<Map<String, Object>> userList = userMapper.queryUserByToken(token);
        if (null == userList || userList.size() <= 0) {
            return ResMsg.fail(ErrorCode.TOKEN_ERROR, "Token非法或Token不存在");
        }
        int uid = Integer.valueOf(String.valueOf(userList.get(0).get("uid")));
        List<Map<String, Object>> addressList = addressMapper.getAddress(uid, uid);
        if (null == addressList || addressList.size() <= 0) {
            return ResMsg.fail(ErrorCode.NO_ADDRESS_ERROR, "该账号下没有对应的地址");
        }
        
        return ResMsg.succWithData(addressMapper.updateAddress(address, phone, name, id));
    }

}
