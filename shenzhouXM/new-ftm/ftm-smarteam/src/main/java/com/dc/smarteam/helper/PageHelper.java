package com.dc.smarteam.helper;

import com.dc.smarteam.common.persistence.Page;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by chenhuae on 2017/1/4.
 */
public class PageHelper {

    private static PageHelper single = null;

    private PageHelper() {
    }

    public static PageHelper getInstance() {
        if (single == null) {
            single = new PageHelper();
        }
        return single;
    }

    /**
     * 获取全部结果，再进行分页
     * 非必要时，使用
     * com.dc.smarteam.common.service.CrudService#findPage(com.dc.smarteam.common.persistence.Page, com.dc.smarteam.common.persistence.DataEntity)
     */
    public <T> void getPage(Class<? extends T> tClass, HttpServletRequest request, HttpServletResponse response, Model model, List<T> list) {
        Page<T> page = new Page<T>(request, response);
        page.setCount(list.size());
        int pageNoIndex = page.getPageNo();
        int pageSize = page.getPageSize();
        List<T> list2 = new ArrayList<T>();
        for (int i = (pageNoIndex - 1) * pageSize; i < pageSize * pageNoIndex; i++) {
            if (list.size() <= i) break;
            T t = list.get(i);
            list2.add(t);
        }
        page.setList(list2);
        model.addAttribute("page", page);
    }

    /**
     * 获取全部结果，再进行分页
     * 非必要时，使用
     * com.dc.smarteam.common.service.CrudService#findPage(com.dc.smarteam.common.persistence.Page, com.dc.smarteam.common.persistence.DataEntity)
     */
    public <T> void getPage(Class<? extends T> tClass, HttpServletRequest request, HttpServletResponse response, Map map, List<T> list) {
        Page<T> page = new Page<T>(request, response);
        page.setCount(list.size());
        int pageNoIndex = page.getPageNo();
        int pageSize = page.getPageSize();
        List<T> list2 = new ArrayList<T>();
        for (int i = (pageNoIndex - 1) * pageSize; i < pageSize * pageNoIndex; i++) {
            if (list.size() <= i) break;
            T t = list.get(i);
            list2.add(t);
        }
        page.setList(list2);
        map.put("page", page);
    }
}
