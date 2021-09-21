package com.amazonaws.lambda.dynamomutant;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LambdaFunctionHandler implements RequestHandler<Request, Object> {

	@Override
	public Object handleRequest(Request request, Context context) {
		AmazonDynamoDB db = AmazonDynamoDBAsyncClientBuilder.defaultClient();
		DynamoDBMapper mapper = new DynamoDBMapper(db);
		MutantTest mutantTest = null;

		switch (request.getHttpMethod()) {
		case "GET":
			if (request.getId() == 0) {
				List<MutantTest> mutantTestList = new ArrayList<>();
				mutantTestList = mapper.scan(MutantTest.class, new DynamoDBScanExpression());
				Stats stat = new Stats();
				for (MutantTest mutantResult : mutantTestList) {
					if (mutantResult.getResult()) {
						stat.setCount_mutant_dna(stat.getCount_mutant_dna() + 1);
					} else {
						stat.setCount_human_dna(stat.getCount_human_dna() + 1);
					}
					if (stat.getCount_human_dna() > 0) {
						stat.setRatio(stat.getCount_mutant_dna() / stat.getCount_human_dna());
					} else if (stat.getCount_human_dna() == 0) {
						stat.setRatio(stat.getCount_mutant_dna());
					}
				}

				return stat;
			} else {
				mutantTest = mapper.load(MutantTest.class, request.getId());
				return mutantTest;
			}
		case "POST":
			mutantTest = new MutantTest();

			// convertimos el array string a string
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < request.getDna().length; i++) {
				stringBuilder.append(request.getDna()[i]);
			}
			mutantTest.setDna(stringBuilder.toString());
			boolean mutant = isMutant(request.getDna());
			mutantTest.setResult(mutant);
			mapper.save(mutantTest);
			return mutantTest;

		case "DELETE":
			mutantTest = mapper.load(MutantTest.class, request.getId());
			mapper.delete(mutantTest);
			return mutantTest;

		}
		return null;
	}

	/**
	 * @param dna
	 * @return boolean Valida que el ADN corresponda a un mutante
	 */
	private static boolean isMutant(String[] dna) {
		// verifica hallazgo vertical
		boolean vertical = validateVertical(dna);
		if (vertical)
			return true;
		// verifica hallazgo horizontal
		for (int i = 0; dna.length > i; i++) {
			boolean horizontal = validateHorizontal(dna[i]);
			if (horizontal)
				return true;
		}
		// verifica hallazgo oblicuo
		boolean diagonal = validateDiagonal(dna);
		if (diagonal)
			return true;

		return false;
	}

	/**
	 * @param dna
	 * @return boolean Valida que un ADN coincida en forma horizontal
	 */
	private static boolean validateHorizontal(String dna) {
		int letterCounter = 1;
		char[] dnaChars = dna.toCharArray();

		for (int i = 0; i < dnaChars.length; i++) {
			// compara consecutivas desde la segunda posición
			if (i == 0)
				continue;
			// resetea contado si la letra de la derecha no es igual a la de la
			// izquierda
			if (dnaChars[i] == dnaChars[i - 1])
				letterCounter++;
			else
				letterCounter = 1;
			// finaliza como verdadero si hay 4 letras iguales consecutivas
			if (letterCounter == 4)
				return true;
		}
		return false;
	}

	/**
	 * @param dna
	 * @return boolean Valida que un ADN coincida en forma vertical
	 */
	public static boolean validateVertical(String[] dna) {
		char[][] matrix = createMatrix(dna);
		int letterCounter = 1;

		for (int i = 0; i < dna.length; i++) {
			for (int j = 0; j < dna.length; j++) {
				// compara consecutivas desde la segunda posición
				if (j == 0)
					continue;
				// resetea contado si la letra de arriba no es igual a la de
				// abajo
				if (matrix[j][i] == matrix[j - 1][i])
					letterCounter++;
				else
					letterCounter = 1;
				// finaliza como verdadero si hay 4 letras iguales consecutivas
				if (letterCounter == 4)
					return true;
			}
		}
		return false;
	}

	/**
	 * @param dna
	 * @return boolean Valida que un ADN coincida en forma oblicua
	 */
	public static boolean validateDiagonal(String[] dna) {
		char[][] matrix = createMatrix(dna);
		int letterCounter = 1;

		for (int i = 0; i < dna.length; i++) {
			for (int j = 0; j < dna.length; j++) {
				// compara consecutivas desde la segunda posición
				if (j == 0)
					continue;
				if (i == 0)
					continue;

				// resetea contado si la letra de arriba no es igual a la de
				// abajo
				if (matrix[j][i] == matrix[j][i - 1]) {
					System.out.println(matrix[j][i - 1]);
					letterCounter++;
				} else
					letterCounter = 1;

				// finaliza como verdadero si hay 4 letras iguales consecutivas
				if (letterCounter == 4)
					return true;

			}
		}
		return false;
	}

	/**
	 * @param dna
	 * @return char[][] Crea una matriz con los datos ingresados
	 */
	private static char[][] createMatrix(String[] dna) {
		System.out.println(dna);
		// crea una matriz cuadrada
		char[][] matrix = new char[dna.length][dna.length];
		for (int i = 0; i < dna.length; i++) {
			for (int j = 0; j < dna[0].length(); j++) {
				// completa la matriz con el dna
				matrix[i][j] = dna[i].toCharArray()[j];
			}
		}
		return matrix;
	}

	public static void main(String[] args) {

		String[] dna = { "ATGCCA", "AAGTCC", "CTACGT", "AACAGG", "GGCCCA", "TCCATG" };

		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < dna.length; i++) {
			stringBuilder.append(dna[i]);
			if (i < dna.length - 1)
				stringBuilder.append(",");
		}
		String joinedString = stringBuilder.toString();
		System.out.println(joinedString);
		// Object obj = handleRequest();
		// String[] dna = { "ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA",
		// "TCACTG" };
		//
		// // imprime matriz generada
		// char[][] matrix = createMatrix(dna);
		// printMatrix(dna, matrix);
		//
		// boolean mutant = isMutant(dna);
		// if (mutant)
		// System.out.println("\n\n:::: MUTANTE DETECTADO ::::");
		// else
		// System.out.println("\n\nEste humano no es mutante");
	}

}
