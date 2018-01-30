package cn.cjp.app.exception;

public class ServiceAssert {

	public static final void assert404(Object t) {
		if (t == null) {
			throw new ServiceException(404);
		}
	}

}
