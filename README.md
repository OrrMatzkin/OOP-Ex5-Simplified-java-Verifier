
 # Simplified Java Verifier

This program was designed and created by Aviram Aloni and Orr Matzkin, as part of The Hebrew University of Jerusalem's *'Introduction to Object-Oriented Programming'* course.

The verifier (s stands for simplified), performs a semantic analysis for the *s-Java* language (s stands for simplified), a critical phase of a compiler. It knows how to read s-Java code and determine its validity, but not to translate it to machine code.

s-Java is a programing language, which only supports a very limited set of Java, all its features are described [here](https://github.com/OrrMatzkin/OOP-Ex5-Simplified-java-Verifier/blob/main/s-Java-specifications.md).

This exercise goals are:
- Implementing with the concepts of Regular Expressions.
- Designing & implementing a complex system.
- Working with the Exceptions mechanism.

![build](https://img.shields.io/badge/build-passing-brightgreen)

![platform](https://camo.githubusercontent.com/fb4912e741e566f3089bd8ca3561a536cc352ecfae75127d2fab3e1852e2234d/68747470733a2f2f696d672e736869656c64732e696f2f62616467652f706c6174666f726d2d6c696e75782532302537432532306d61636f7325323025374325323077696e646f77732d6c6967687467726579)

## Requirements
The s-Java Verifier requires the followin to run:

- openjdk-16 version 16.0.1+


## Run Locally

Clone the project

```bash
 git clone https://github.com/OrrMatzkin/OOP-Ex5-Simplified-java-Verifier.git
```

Go to the project directory

```bash
 cd OOP-Ex5-Simplified-java-Verifier
```

Start the analysis for a specific sjava file , run the   sjavac file from the oop.ex5.main package

```bash
 java oop.ex5.main.Sjavac <source-file-name>
```
The output of the program is a single digit:
- 0 – if the code is legal.  
- 1 – if the code is illegal.  
- 2 – in case of IO errors.

## Documentation
To open the *s-Java*  Verifier  Java documentation clone the project


```bash
 git clone https://github.com/OrrMatzkin/OOP-Ex5-Simplified-java-Verifier.git
```
Go to the documentation directory
```bash
 cd OOP-Ex5-Simplified-java-Verifier/documentation
```

open the `index.html` file with your web broswer.
