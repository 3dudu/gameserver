package com.xuegao.core.db.po;


public class CacheKeyVo {
	Class<? extends BasePo> clazz;
	Long id;
	public CacheKeyVo(Class<? extends BasePo> clazz, Long id) {
		super();
		this.clazz = clazz;
		this.id = id;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		Long result = 1L;
		result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
		result = prime * result + ((id == null) ? 0 : id);
		return result.intValue();
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		CacheKeyVo other = (CacheKeyVo) obj;
		if (clazz == null) {
			if (other.clazz != null)
				return false;
		} else if (clazz!=other.clazz)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	public Class<? extends BasePo> getClazz() {
		return clazz;
	}
	public void setClazz(Class<? extends BasePo> clazz) {
		this.clazz = clazz;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
}
