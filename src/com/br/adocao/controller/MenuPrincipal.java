package com.br.adocao.controller;

import java.util.InputMismatchException;
import java.util.Scanner;

public class MenuPrincipal {
    private static final Scanner sc = new Scanner(System.in);
    private static final PetController PetController = new PetController();
    private static final FormularioController FormularioController = new FormularioController();

    public void informarMenuPrincipal() {
        int resposta;
        do {
            System.out.println("-----------------------------------------------------------------------------------");
            System.out.println("1 - Iniciar sistema para cadastro de PETS");
            System.out.println("2 - Iniciar sistema para alterar formulario");
            System.out.println("3 - Encerrar programa");
            System.out.print("R: ");
            try {
                resposta = sc.nextInt();
                sc.nextLine();
                switch (resposta) {
                    case 1 -> PetController.sistemaDePets();
                    case 2 -> FormularioController.informarMenu();
                }
                if (resposta > 3) System.out.println("Digite uma opção válida!");
            }catch (InputMismatchException e) {
                System.out.println("Digite apenas números!");
                sc.nextLine();
                resposta = 0;
            }
        } while (resposta != 3);
        sc.close();
    }
}
