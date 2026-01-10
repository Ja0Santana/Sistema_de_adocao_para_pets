package com.br.adocao.interfaces;

import java.io.IOException;
import java.util.List;

public interface MenuRepository {
    public List<String> carregarOpcoesMenu() throws IOException;

}
