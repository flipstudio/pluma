package com.flipstudio.pluma;

/**
 * Created by Pietro Caselani
 * On 4/15/15
 * Pluma
 */
public abstract class SQLiteFunction {
	//region Fields
	private final long mNativePtr;
	//endregion

	//region Native
	private native long init();

	private native void setErrorResult(long nativePtr, String message, int code);

	private native void setDoubleResult(long nativePtr, double value);

	private native void setIntResult(long nativePtr, int value);

	private native void setLongResult(long nativePtr, long value);

	private native void setStringResult(long nativePtr, String value);

	private native void setNullResult(long nativePtr);

	//TODO setResultBlob and setResultValue

	private native double getDoubleArg(long nativePtr, int index);

	private native int getIntArg(long nativePtr, int index);

	private native long getLongArg(long nativePtr, int index);

	private native String getStringArg(long nativePtr, int index);

	//TODO getBlobArg
	//endregion

	//region Constructors
	public SQLiteFunction() {
		mNativePtr = init();
	}
	//endregion

	//region Results
	protected final void setErrorResult(String message, int code) {
		setErrorResult(mNativePtr, message, code);
	}

	protected final void setDoubleResult(double value) {
		setDoubleResult(mNativePtr, value);
	}

	protected final void setIntResult(int value) {
		setIntResult(mNativePtr, value);
	}

	protected final void setLongResult(long value) {
		setLongResult(mNativePtr, value);
	}

	protected final void setStringResult(String value) {
		setStringResult(mNativePtr, value);
	}

	protected final void setNullResult() {
		setNullResult(mNativePtr);
	}
	//endregion

	//region Args
	protected final double getDoubleArg(int index) {
		return getDoubleArg(mNativePtr, index);
	}

	protected final int getIntArg(int index) {
		return getIntArg(mNativePtr, index);
	}

	protected final long getLongArg(int index) {
		return getLongArg(mNativePtr, index);
	}

	protected final String getStringArg(int index) {
		return getStringArg(mNativePtr, index);
	}
	//endregion

	//region Package
	final long getNativeHandler() {
		return mNativePtr;
	}
	//endregion

	//region Abstract
	// Called from native code
	protected abstract void run(int argc);
	//endregion
}
