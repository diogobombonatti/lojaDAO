package com.example;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.example.data.ProdutoDao;
import com.example.model.Produto;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class PrimaryController implements Initializable {

    @FXML
    TextField txtMarca;
    @FXML
    TextField txtModelo;
    @FXML
    TextField txtValor;

    @FXML
    TableView<Produto> tabela;

    @FXML
    TableColumn<Produto, String> colMarca;
    @FXML
    TableColumn<Produto, String> colModelo;
    @FXML
    TableColumn<Produto, BigDecimal> colValor;

    public void adicionar() {
        var produto = new Produto(
                null,
                txtMarca.getText(),
                txtModelo.getText(),
                new BigDecimal(txtValor.getText()));

        try {
            ProdutoDao.inserir(produto);
            tabela.getItems().add(produto);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void excluirProduto() {
        Produto produtoSelecionado = tabela.getSelectionModel().getSelectedItem();
        if (produtoSelecionado != null) {
            try {
                ProdutoDao.excluir(produtoSelecionado);
                tabela.getItems().remove(produtoSelecionado);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void editarProduto() {
        Produto produtoSelecionado = tabela.getSelectionModel().getSelectedItem();
        if (produtoSelecionado != null) {
            try {
                // Abra a interface de edição aqui, preenchendo os campos de texto com os
                // valores atuais do produto
                txtMarca.setText(produtoSelecionado.getMarca());
                txtModelo.setText(produtoSelecionado.getModelo());
                txtValor.setText(produtoSelecionado.getValor().toString());

                // Atualize a interface para permitir que o usuário faça as alterações

                // Quando o usuário confirmar as alterações, atualize o objeto Produto e o banco
                // de dados
                Produto novoProduto = new Produto(
                        produtoSelecionado.getId(),
                        txtMarca.getText(),
                        txtModelo.getText(),
                        new BigDecimal(txtValor.getText()));

                ProdutoDao.atualizar(novoProduto);

                // Atualize a tabela para refletir as alterações
                tabela.getItems().set(tabela.getSelectionModel().getSelectedIndex(), novoProduto);

                // Limpe os campos de texto após a edição
                txtMarca.clear();
                txtModelo.clear();
                txtValor.clear();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void carregar() {
        tabela.getItems().clear();
        try {
            var produtos = ProdutoDao.buscarTodos();
            produtos.forEach(veiculo -> tabela.getItems().add(veiculo));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));

        carregar();
    }

}
