package org.ww.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.ww.domain.TelDoc;

public interface HotlineDocService {

    IPage<TelDoc> query(Long telGroupId, String keywords, Integer pageNum, Integer pageSize);

    int remove(Long telGroupId, Long id);

    TelDoc add(TelDoc doc);

    IPage<TelDoc> list(Long telGroupId, Integer pageNum, Integer pageSize);

    TelDoc get(Long telGroupId, Long id);

    int update(Long telGroupId, TelDoc doc);
}
