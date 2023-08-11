package com.icegame.sysmanage.mapper;

import java.util.List;

import com.icegame.sysmanage.entity.PfOptions;

public interface PfOptionsMapper {
		
	public List<PfOptions> getPfOptionsList(PfOptions pfOptions);
			
	public PfOptions getPfOptionsById(Long id);
	
	public List<PfOptions> checkExistPfOptions(PfOptions pfOptions);
	
	public List<PfOptions> getPfOptionsAll();
	
	public boolean addPfOptions(PfOptions pfOptions);
	
	public Long addPfOptionsReturnId(PfOptions pfOptions);
	
	public boolean delPfOptions(Long id);

	public boolean updatePfOptions(PfOptions pfOptions);

}
