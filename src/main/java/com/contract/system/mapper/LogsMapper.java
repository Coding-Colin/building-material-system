package com.contract.system.mapper;

import com.contract.system.bean.Logs;

import java.util.List;

public interface LogsMapper {

	void add(Logs logs);

	List<Logs> getAll();

}
