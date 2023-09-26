package com.example.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.model.Produto;

public class ProdutoDao {

    private static final String URL = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL";
    private static final String USER = "rm551694";
    private static final String PASS = "061292";

    public static void inserir(Produto produto) throws SQLException {
        try (Connection conexao = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement comando = conexao.prepareStatement(
                "INSERT INTO produtos (marca, modelo, valor) VALUES (?, ?, ?)")) {
            comando.setString(1, produto.getMarca());
            comando.setString(2, produto.getModelo());
            comando.setBigDecimal(3, produto.getValor());
            comando.executeUpdate();
        }
    }

   public static void excluir(Produto produto) throws SQLException {
    if (produto == null || produto.getId() == null) {
        // Lide com o caso em que produto ou produto.getId() é nulo
        return;
    }
    
    try (Connection conexao = DriverManager.getConnection(URL, USER, PASS);
         PreparedStatement comando = conexao.prepareStatement(
            "DELETE FROM produtos WHERE id = ?")) {
        comando.setLong(1, produto.getId());
        comando.executeUpdate();
    }
}

    public static void atualizar(Produto produto) throws SQLException {
        try (Connection conexao = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement comando = conexao.prepareStatement(
                "UPDATE produtos SET marca = ?, modelo = ?, valor = ? WHERE id = ?")) {
            comando.setString(1, produto.getMarca());
            comando.setString(2, produto.getModelo());
            comando.setBigDecimal(3, produto.getValor());
            comando.setLong(4, produto.getId());
            comando.executeUpdate();
        }
    }

    public static List<Produto> buscarTodos() throws SQLException {
        var lista = new ArrayList<Produto>();

        try (Connection conexao = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement comando = conexao.prepareStatement("SELECT * FROM produtos");
             var resultado = comando.executeQuery()) {

            while (resultado.next()) {
                lista.add(new Produto(
                        resultado.getLong("id"),
                        resultado.getString("marca"),
                        resultado.getString("modelo"),
                        resultado.getBigDecimal("valor")));
            }
        }

        return lista;
    }
}
