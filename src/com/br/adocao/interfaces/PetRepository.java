package com.br.adocao.interfaces;

import com.br.adocao.model.Pet;

import java.io.IOException;
import java.util.List;

public interface PetRepository {
    List<Pet> carregarPets () throws IOException;
    void salvar (Pet pet) throws IOException;
    void deletar (Pet pet) throws IOException;
}
