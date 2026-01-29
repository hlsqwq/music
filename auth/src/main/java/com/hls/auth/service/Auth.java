package com.hls.auth.service;

import com.hls.auth.po.AuthParams;
import com.hls.auth.po.User;

public interface Auth {


    User auth(AuthParams authParams);
}
