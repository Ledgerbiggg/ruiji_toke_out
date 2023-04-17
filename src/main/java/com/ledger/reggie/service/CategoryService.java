package com.ledger.reggie.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ledger.reggie.common.R;
import com.ledger.reggie.domian.Category;

import java.util.List;

/**
 * @author ledger
 * @version 1.0
 **/

public interface CategoryService extends IService<Category> {
    R<Page<Category>> getPageList(Long page, Long pageSize);

    R<String> delMenu(Long ids);

    R<String> edit(Category category);
}
