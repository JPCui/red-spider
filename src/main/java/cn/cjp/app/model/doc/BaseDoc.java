package cn.cjp.app.model.doc;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class BaseDoc implements Serializable {
	
	private String _id;
	
	private Date _updateDate = new Date();

}
