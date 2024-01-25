package org.ww.service;

import org.ww.domain.TelCommonCode;

import java.util.List;

public interface HotlineCommonCodeService {
    List<TelCommonCode> listByPage(String pageName);
}
