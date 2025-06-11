import java.util.Scanner;

public class SistemaBancario {
  // Array est�tico para armazenar at� 100 contas banc�rias
  static Conta[] cadastro = new Conta[100];
  // Contador que controla quantas contas j� foram cadastradas
  static int cont = 0;
  // Scanner para ler entrada do usu�rio
  static Scanner scanner = new Scanner(System.in);
  // Objeto para manipula��o de arquivo que armazena informa��es banc�rias
  static Arquivo bancoInfo = new Arquivo("InformacoesBancarias.txt");
  
  public static void main(String[] arg) {
    // Carrega as contas armazenadas no arquivo ao iniciar o sistema
    carregarContas();
    int escolha = -1;
    
    // Loop principal que exibe o menu e processa as op��es escolhidas
    
    while (escolha != 0) 
    {
      System.out.println("\n----- SISTEMA BANCARIO ALP -----");
      System.out.println("1 - Cadastrar conta");
      System.out.println("2 - Deposito bancario");
      System.out.println("3 - Saque bancario");
      System.out.println("4 - Exibir saldo da conta");
      System.out.println("5 - Transferir valor para outra conta");
      System.out.println("0 - Sair do sistema");
      System.out.print("Escolha uma opcao: ");
      
      escolha = scanner.nextInt();
      scanner.nextLine(); // Limpa buffer do scanner ap�s leitura num�rica
      
      switch (escolha) 
      {
        case 0:
          // Salva as contas no arquivo antes de sair do sistema
          salvarContas();
          System.out.println("Saindo do sistema bancario...");
          break;
        case 1:
          cadastrarConta();
          break;
          
        case 2:
          deposito();
          break;
          
        case 3:
          saque();
          break;
          
        case 4:
          saldo();
          break;
          
        case 5:
          transferir();
          break;
          
        default:
          System.out.println("Opcao invalida!");
      }
    }
  }
  
  // Classe que representa uma conta banc�ria com n�mero, titular, saldo e senha
  
  public static class Conta {
    int numero;       // N�mero �nico da conta
    String titular;   // Nome do titular da conta
    double saldo;     // Saldo atual da conta
    String senha;     // Senha criada pelo usu�rio para autentica��o
    
    // Construtor da conta recebe dados incluindo a senha personalizada
    
    public Conta(int num, String nomeTitular, double saldoInicial, String senhaInformada) {
      numero = num;
      titular = nomeTitular;
      saldo = saldoInicial;
      senha = senhaInformada;
    }
  }
  
  // M�todo para carregar contas do arquivo, recuperando todas as contas previamente salvas
  
  static void carregarContas() {
    bancoInfo.abrirLeitura();
    String linha = bancoInfo.lerLinha();
    
    // Continua lendo at� acabar o arquivo ou atingir o limite de 100 contas
    
    while (linha != null && cont < 100) 
    {
      String[] dados = linha.split(";");
      
      // Verifica se a linha possui todos os dados necess�rios
      
      if (dados.length < 4) 
      {
        System.out.println("Erro: linha com dados incompletos no arquivo.");
        linha = bancoInfo.lerLinha();
        continue;
      }
      
      // Converte dados do arquivo para os tipos apropriados
      
      int numero = Integer.parseInt(dados[0]);
      String titular = dados[1];
      double saldo = Double.parseDouble(dados[2]);
      String senha = dados[3];
      
      // Cria a conta e adiciona ao cadastro
      
      cadastro[cont] = new Conta(numero, titular, saldo, senha);
      cont++;
      
      linha = bancoInfo.lerLinha();
    }
    
    bancoInfo.fecharArquivo();
  }
  
  // M�todo para salvar todas as contas no arquivo, incluindo senha para persist�ncia
  
  static void salvarContas() {
    bancoInfo.abrirEscrita();
    
    // Para cada conta cadastrada, formata e grava os dados no arquivo
    
    for (int i = 0; i < cont; i++) 
    {
      String linha = cadastro[i].numero + ";" +
        cadastro[i].titular + ";" +
        cadastro[i].saldo + ";" +
        cadastro[i].senha;
      bancoInfo.escreverLinha(linha);
    }
    
    bancoInfo.fecharArquivo();
  }
  
  // M�todo para cadastrar nova conta solicitando dados e senha do usu�rio
  
  static void cadastrarConta() 
  {
    if (cont >= 100) 
    {
      System.out.println("Limite de contas cadastradas atingido!");
      return;
    }
    
    System.out.print("\nNumero da conta: ");
    int numero = scanner.nextInt();
    scanner.nextLine();
    
    System.out.print("Nome do titular: ");
    String titular = scanner.nextLine();
    
    System.out.print("Saldo inicial: ");
    double saldo = scanner.nextDouble();
    scanner.nextLine();
    
    System.out.print("Crie uma senha para a conta: ");
    String senha = scanner.nextLine();
    
    // Cria e armazena a nova conta com senha personalizada
    
    cadastro[cont] = new Conta(numero, titular, saldo, senha);
    cont++;
    
    System.out.println("Conta cadastrada com sucesso!");
  }
  
  // M�todo para realizar dep�sito em uma conta autenticada
  
  static void deposito() {
    
    System.out.print("\nNumero da conta: ");
    int numero = scanner.nextInt();
    scanner.nextLine();
    
    Conta conta = buscarConta(numero);
    
    if (conta == null) 
    {
      System.out.println("Conta nao encontrada!");
      return;
    }
    
    // Requer autentica��o antes de permitir dep�sito
    
    if (!autenticacao(conta)) 
    {
      System.out.println("Senha incorreta. Operacao cancelada.");
      return;
    }
    
    System.out.print("Valor do deposito: ");
    double valor = scanner.nextDouble();
    scanner.nextLine();
    
    // Atualiza saldo da conta
    
    conta.saldo += valor;
    System.out.println("Deposito realizado com sucesso!");
  }
  
  // M�todo para realizar saque de uma conta autenticada e com saldo suficiente
  
  static void saque() {
    
    System.out.print("\nNumero da conta: ");
    int numero = scanner.nextInt();
    scanner.nextLine();
    
    Conta conta = buscarConta(numero);
    
    if (conta == null) 
    {
      System.out.println("Conta nao encontrada!");
      return;
    }
    
    // Autentica��o da conta para seguran�a
    
    if (!autenticacao(conta))
    {
      System.out.println("Senha incorreta. Operacao cancelada.");
      return;
    }
    
    System.out.print("Valor do saque: ");
    double valor = scanner.nextDouble();
    scanner.nextLine();
    
    // Verifica saldo suficiente antes de permitir saque
    
    if (valor > conta.saldo) 
    {
      System.out.println("Saldo insuficiente!");
    } else {
      conta.saldo -= valor;
      System.out.println("Saque realizado com sucesso!");
    }
  }
  
  // M�todo para exibir saldo da conta ap�s autentica��o
  
  static void saldo() {
    System.out.print("\nNumero da conta: ");
    int numero = scanner.nextInt();
    scanner.nextLine();
    
    Conta conta = buscarConta(numero);
    
    if (conta == null)
    {
      System.out.println("Conta nao encontrada!");
      return;
    }
    
    // Autentica��o para proteger dados da conta
    
    if (!autenticacao(conta)) 
    {
      System.out.println("Senha incorreta. Operacao cancelada.");
      return;
    }
    
    System.out.println("Saldo atual: " + conta.saldo);
  }
  
  // M�todo auxiliar para buscar uma conta pelo n�mero informado
  
  static Conta buscarConta(int numero) {
    
    for (int i = 0; i < cont; i++) 
    {
      if (cadastro[i].numero == numero) 
      {
        return cadastro[i];
      }
    }
    
    return null; // Retorna null se a conta n�o for encontrada
  }
  
  // M�todo para autenticar o usu�rio solicitando a senha da conta
  
  static boolean autenticacao(Conta conta) {
    
    System.out.print("Digite a senha da conta: ");
    String senhaDigitada = scanner.nextLine();
    return conta.senha.equals(senhaDigitada);
  }
  
  // M�todo para realizar transfer�ncia entre contas ap�s autentica��o da conta origem
  
  static void transferir() {
    
    System.out.print("\nNumero da conta origem: ");
    int numOrigem = scanner.nextInt();
    
    scanner.nextLine();
    
    Conta origem = buscarConta(numOrigem);
    
    if (origem == null) 
    {
      System.out.println("Conta de origem nao encontrada!");
      return;
    }
    
    // Autentica conta origem para garantir seguran�a
    
    if (!autenticacao(origem)) 
    {
      System.out.println("Senha incorreta. Operacao cancelada.");
      return;
    }
    
    System.out.print("Numero da conta destino: ");
    int numDestino = scanner.nextInt();
    scanner.nextLine();
    Conta destino = buscarConta(numDestino);
    
    if (destino == null) 
    {
      System.out.println("Conta de destino nao encontrada!");
      return;
    }
    
    System.out.print("Valor da transferencia: ");
    double valor = scanner.nextDouble();
    scanner.nextLine();
    
    // Verifica saldo suficiente para transfer�ncia
    
    if (valor > origem.saldo) 
    {
      System.out.println("Saldo insuficiente!");
      return;
    }
    
    // Atualiza saldos das contas envolvidas na transfer�ncia
    
    origem.saldo -= valor;
    destino.saldo += valor;
    
    System.out.println("Transferencia realizada com sucesso!");
  }
}
