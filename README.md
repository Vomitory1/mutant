# Test para Mutantes

## Descripcion:
api para probar un ADN y validar si es mutante o no, API rest con Maven en Java desplegada en Lambda de AWS y Base de datos DynamoDB

## ¿Como probarla?
Es realmente sencillo tenemos 2 url: 
  La primera es para hacer el test del ADN y verificar de acuerdo a la logica del programa si este ADN pertenece a un mutante,
  esta es un metodo POST, donde enviamos el ADN en un array de string asi:
    https://gnz7ynzdvh.execute-api.us-east-1.amazonaws.com/dev/mutant
    
    en el body ponemos
    {
        "test": {
            "dna": ["ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA","TCACTG"]
        }
    }
  
La Segunda
