package com.dcfs.esb.ftp.spring.core.repository;

import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface LongBaseRepository<T> extends BaseRepository<T, Long> {
}
