package com.serverMonitor.utils.page;

import org.springframework.data.domain.*;

import java.util.List;

public class InnerPage<T> {

    private List <T> list;

    public InnerPage(List<T> list){
        this.list = list;
    }


    public PageImpl getPageInObjectList(int page, int size){
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.Direction.DESC, "id");
        int endIndex = size > list.size() ? list.size() :  size * page;
        int startIndex = size > list.size() ? 0 : endIndex - size;

        if(page*size >= list.size()){ endIndex = list.size();}


       // return new PageImpl(list.subList(startIndex,endIndex),pageRequest,list.size());
        return new PageImpl(list,pageRequest,list.size());
    }

    public static <T> Page<T> getPageFromList(List<T> list, Pageable pageable) {

        if (list.isEmpty()) {
            return new PageImpl<>(list);
        }

        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), list.size());

        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }

}
