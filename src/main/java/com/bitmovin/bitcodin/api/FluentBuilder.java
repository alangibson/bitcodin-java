package com.bitmovin.bitcodin.api;

/**
 * Builders have 2 types of mutator methods:
 * - Methods named like add*() first add elements to an underlying collection,
 *   then return a refrence to a builder.
 * - Methods named like with*() first set a property then return 'this' builder. 
 * 
 * After calling mutator methods, call either build() to produce a config object
 * or done() to return the parent builder.
 * 
 * @author alangibson
 *
 * @param <P> Type of the parent builder, or same as S if no parent builder.
 * @param <S> Type of the config object we will build.
 */
public interface FluentBuilder<P,S> {

	/**
	 * 1. Set any configuration necessary on the parent builder (if any).
	 * 2. Return reference to the config object we built. 
	 * 
	 * @return 
	 */
	public S build();
	
	/**
	 * If this builder is nested inside another call (i.e. was created via
	 * method call to another builder):
	 *  
	 * 1. Set any configuration necessary on the parent builder
	 * 2. Return reference to the parent builder. 
	 * 
	 * In the case that this builder is not nested inside a call to another builder,
	 * behaves exactly like build().
	 * 
	 * @return Parent builder or our config object if not a nested call.
	 */
	public P done();
	
}
