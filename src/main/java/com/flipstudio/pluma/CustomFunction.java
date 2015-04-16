package com.flipstudio.pluma;

/**
 * Created by Pietro Caselani
 * On 4/15/15
 * Pluma
 */
public final class CustomFunction {
	//region Fields
	final String mName;
	final int mNumArgs;
	final Callback mCallback;
	//endregion

	//region Constructors
	public CustomFunction(String name, int numArgs, Callback callback) {
		if (name == null) throw new IllegalArgumentException("Name can't be null");

		mName = name;
		mNumArgs = numArgs;
		mCallback = callback;
	}
	//endregion

	//region Private
	//Called from native code
	private void dispatchCallback(String[] args) {
		if (mCallback != null) mCallback.callback(args);
	}
	//endregion

	public interface Callback {
		void callback(String[] args);
	}
}
