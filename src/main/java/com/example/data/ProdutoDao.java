package com.example.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.model.Produto;

public class ProdutoDao {
    static final String URL = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL";
    static final String USER = "rm551694";
    static final String PASS = "061292";

    // Método para obter uma conexão com o banco de dados
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void inserir(Produto produto) throws SQLException {
        try (Connection conexao = getConnection()) {
            String sql = "INSERT INTO produtos (marca, modelo, valor) VALUES (?, ?, ?)";
            try (PreparedStatement comando = conexao.prepareStatement(sql)) {
                comando.setString(1, produto.getMarca());
                comando.setString(2, produto.getModelo());
                comando.setBigDecimal(3, produto.getValor());
                comando.executeUpdate();
            }
        }
    }

    public static void excluir(Produto produto) throws SQLException {
        try (Connection conexao = getConnection()) {
            String sql = "DELETE FROM produtos WHERE id = ?";
            try (PreparedStatement comando = conexao.prepareStatement(sql)) {
                comando.setLong(1, produto.getId());
                comando.executeUpdate();
            }
        }
    }

    public static void atualizar(Produto produto) throws SQLException {
        try (Connection conexao = getConnection()) {
            String sql = "UPDATE produtos SET marca = ?, modelo = ?, valor = ? WHERE id = ?";
            try (PreparedStatement comando = conexao.prepareStatement(sql)) {
                comando.setString(1, produto.getMarca());
                comando.setString(2, produto.getModelo());
                comando.setBigDecimal(3, produto.getValor());
                comando.setLong(4, produto.getId());
                comando.executeUpdate();
            }
        }
    }

    public static List<Produto> buscarTodos() throws SQLException {
        List<Produto> lista = new ArrayList<>();
        try (Connection conexao = getConnection()) {
            String sql = "SELECT * FROM produtos";
            try (PreparedStatement comando = conexao.prepareStatement(sql)) {
                try (ResultSet resultado = comando.executeQuery()) {
                    while (resultado.next()) {
                        lista.add(new Produto(
                                resultado.getLong("id"),
                                resultado.getString("marca"),
                                resultado.getString("modelo"),
                                resultado.getBigDecimal("valor")));
                    }
                }
            }
        }
        return lista;
    }

    public static Produto obterProdutoPeloModelo(String modelo) throws SQLException {
        try (Connection conexao = getConnection()) {
            String sql = "SELECT * FROM produtos WHERE modelo = ?";
            try (PreparedStatement comando = conexao.prepareStatement(sql)) {
                comando.setString(1, modelo);
                try (ResultSet resultado = comando.executeQuery()) {
                    if (resultado.next()) {
                        return new Produto(
                                resultado.getLong("id"),
                                resultado.getString("marca"),
                                resultado.getString("modelo"),
                                resultado.getBigDecimal("valor"));
                    }
                }
            }
        }
        return null; // Retorna null se o produto não for encontrado
    }
}
