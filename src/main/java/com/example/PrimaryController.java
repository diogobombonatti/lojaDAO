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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.BigDecimalStringConverter;

public class PrimaryController implements Initializable {

    @FXML
    private TextField txtMarca;
    @FXML
    private TextField txtModelo;
    @FXML
    private TextField txtValor;
    @FXML
    private TableView<Produto> tabela;
    @FXML
    private TableColumn<Produto, String> colMarca;
    @FXML
    private TableColumn<Produto, String> colModelo;
    @FXML
    private TableColumn<Produto, BigDecimal> colValor;

    private ProdutoDao produtoDao; // Declare a variável produtoDao

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colMarca.setCellFactory(TextFieldTableCell.forTableColumn());
        colMarca.setOnEditCommit(e -> atualizarProduto(e.getRowValue(), "marca", e.getNewValue()));

        colModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colModelo.setCellFactory(TextFieldTableCell.forTableColumn());
        colModelo.setOnEditCommit(e -> atualizarProduto(e.getRowValue(), "modelo", e.getNewValue()));

        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colValor.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter()));
        colValor.setOnEditCommit(e -> atualizarProduto(e.getRowValue(), "valor", e.getNewValue()));

        // Inicialize o produtoDao aqui
        produtoDao = new ProdutoDao();

        carregarProdutos();
    }

    @FXML
    public void adicionar() {
        var marca = txtMarca.getText();
        var modelo = txtModelo.getText();
        var valorTexto = txtValor.getText();

        if (!marca.isEmpty() && !modelo.isEmpty() && !valorTexto.isEmpty()) {
            try {
                BigDecimal valor = new BigDecimal(valorTexto);

                var produto = new Produto(null, marca, modelo, valor);

                // Insira o produto no banco de dados
                ProdutoDao.inserir(produto);

                // Após a inserção bem-sucedida, obtenha o ID atribuído ao produto
                // consultando o banco de dados
                produto = ProdutoDao.obterProdutoPeloModelo(produto.getModelo());

                // Verifique se o produto não é nulo antes de adicioná-lo à tabela
                if (produto != null) {
                    tabela.getItems().add(produto);

                    // Limpe os campos de entrada após a adição
                    txtMarca.clear();
                    txtModelo.clear();
                    txtValor.clear();
                } else {
                    // Trate o caso em que o produto não pôde ser obtido a partir do banco de dados
                    System.out.println("Falha ao obter o ID do produto após a inserção.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                // Trate o caso em que o valor não é um número válido
                System.out.println("O valor não é um número válido.");
            }
        } else {
            // Trate o caso em que um dos campos está vazio
            System.out.println("Preencha todos os campos antes de adicionar o produto.");
        }
    }

    @FXML
    public void excluirProduto() {
        Produto produtoSelecionado = tabela.getSelectionModel().getSelectedItem();
        if (produtoSelecionado != null) {
            if (produtoSelecionado.getId() != null) { // Verifique se o ID é válido
                try {
                    ProdutoDao.excluir(produtoSelecionado);
                    tabela.getItems().remove(produtoSelecionado);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                // Trate o caso em que o ID não é válido (por exemplo, exibir uma mensagem de
                // erro)
                System.out.println("O produto não possui um ID válido.");
            }
        } else {
            // Tratar o caso em que nenhum produto está selecionado
        }
    }

    @FXML
    public void editarProduto() {
        Produto produtoSelecionado = tabela.getSelectionModel().getSelectedItem();
        if (produtoSelecionado != null) {
            try {
                var marca = txtMarca.getText();
                var modelo = txtModelo.getText();
                var valorTexto = txtValor.getText();

                if (!marca.isEmpty() && !modelo.isEmpty() && !valorTexto.isEmpty()) {
                    BigDecimal novoValor = new BigDecimal(valorTexto);
                    produtoSelecionado.setMarca(marca);
                    produtoSelecionado.setModelo(modelo);
                    produtoSelecionado.setValor(novoValor);

                    ProdutoDao.atualizar(produtoSelecionado);

                    int selectedIndex = tabela.getSelectionModel().getSelectedIndex();
                    tabela.getItems().set(selectedIndex, produtoSelecionado);

                    txtMarca.clear();
                    txtModelo.clear();
                    txtValor.clear();
                } else {
                    // Trate o caso em que um dos campos está vazio
                    System.out.println("Preencha todos os campos antes de editar o produto.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                // Trate o caso em que o valor não é um número válido
                System.out.println("O valor não é um número válido.");
            }
        }
    }

    private void carregarProdutos() {
        tabela.getItems().clear();
        try {
            var produtos = ProdutoDao.buscarTodos();
            tabela.getItems().addAll(produtos);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void atualizarProduto(Produto produto, String propriedade, Object novoValor) {
        if ("marca".equals(propriedade)) {
            produto.setMarca((String) novoValor);
        } else if ("modelo".equals(propriedade)) {
            produto.setModelo((String) novoValor);
        } else if ("valor".equals(propriedade)) {
            produto.setValor((BigDecimal) novoValor);
        }

        try {
            ProdutoDao.atualizar(produto);
            tabela.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
