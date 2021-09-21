package com.amazonaws.lambda.dynamomutant;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "MutantTest")
public class MutantTest {

	@DynamoDBHashKey
	private String dna;
	@DynamoDBAttribute
	private boolean result;

	public MutantTest() {
	}

	public MutantTest(String dna, boolean result) {
		this.dna = dna;
		this.result = result;
	}

	public String getDna() {
		return dna;
	}

	public void setDna(String dna) {
		this.dna = dna;
	}

	public boolean getResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}
}
