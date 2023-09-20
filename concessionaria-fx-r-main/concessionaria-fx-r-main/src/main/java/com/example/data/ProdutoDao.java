package com.example.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.model.Produto;

public class ProdutoDao {

    static final String URL = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL";
    static final String USER = "rm551694";
    static final String PASS = "061292";

    public static void inserir(Produto produto) throws SQLException {
        var conexao = DriverManager.getConnection(URL, USER, PASS);

        var sql = "INSERT INTO produtos (marca, modelo, valor) VALUES (?, ?, ?) ";
        var comando = conexao.prepareStatement(sql);
        comando.setString(1, produto.getMarca());
        comando.setString(2, produto.getModelo());
        comando.setBigDecimal(3, produto.getValor());
        comando.executeUpdate();

        conexao.close();

    }

    public static void excluir(Produto produto) throws SQLException {
        Connection conexao = DriverManager.getConnection(URL, USER, PASS);
        String sql = "DELETE FROM produtos WHERE id = ?";
        PreparedStatement comando = conexao.prepareStatement(sql);
        comando.setLong(1, produto.getId());
        comando.executeUpdate();
        conexao.close();
    }

    public static void atualizar(Produto produto) throws SQLException {
        Connection conexao = DriverManager.getConnection(URL, USER, PASS);
        String sql = "UPDATE produtos SET marca = ?, modelo = ?, valor = ? WHERE id = ?";
        PreparedStatement comando = conexao.prepareStatement(sql);
        comando.setString(1, produto.getMarca());
        comando.setString(2, produto.getModelo());
        comando.setBigDecimal(3, produto.getValor());
        comando.setLong(4, produto.getId());
        comando.executeUpdate();
        conexao.close();
    }

    public static List<Produto> buscarTodos() throws SQLException {
        var lista = new ArrayList<Produto>();

        var conexao = DriverManager.getConnection(URL, USER, PASS);
        var comando = conexao.prepareStatement("SELECT * FROM produtos");
        var resultado = comando.executeQuery();

        while (resultado.next()) {
            lista.add(new Produto(
                    resultado.getLong("id"),
                    resultado.getString("marca"),
                    resultado.getString("modelo"),
                    resultado.getBigDecimal("valor")));
        }

        conexao.close();
        return lista;
    }
}
