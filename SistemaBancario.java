import java.util.Scanner;

public class SistemaBancario {
  // Array estático para armazenar até 100 contas bancárias
  static Conta[] cadastro = new Conta[100];
  // Contador que controla quantas contas já foram cadastradas
  static int cont = 0;
  // Scanner para ler entrada do usuário
  static Scanner scanner = new Scanner(System.in);
  // Objeto para manipulação de arquivo que armazena informações bancárias
  static Arquivo bancoInfo = new Arquivo("InformacoesBancarias.txt");
  
  public static void main(String[] arg) {
    // Carrega as contas armazenadas no arquivo ao iniciar o sistema
    carregarContas();
    int escolha = -1;
    
    // Loop principal que exibe o menu e processa as opções escolhidas
    
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
      scanner.nextLine(); // Limpa buffer do scanner após leitura numérica
      
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
  
  // Classe que representa uma conta bancária com número, titular, saldo e senha
  
  public static class Conta {
    int numero;       // Número único da conta
    String titular;   // Nome do titular da conta
    double saldo;     // Saldo atual da conta
    String senha;     // Senha criada pelo usuário para autenticação
    
    // Construtor da conta recebe dados incluindo a senha personalizada
    
    public Conta(int num, String nomeTitular, double saldoInicial, String senhaInformada) {
      numero = num;
      titular = nomeTitular;
      saldo = saldoInicial;
      senha = senhaInformada;
    }
  }
  
  // Método para carregar contas do arquivo, recuperando todas as contas previamente salvas
  
  static void carregarContas() {
    bancoInfo.abrirLeitura();
    String linha = bancoInfo.lerLinha();
    
    // Continua lendo até acabar o arquivo ou atingir o limite de 100 contas
    
    while (linha != null && cont < 100) 
    {
      String[] dados = linha.split(";");
      
      // Verifica se a linha possui todos os dados necessários
      
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
  
  // Método para salvar todas as contas no arquivo, incluindo senha para persistência
  
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
  
  // Método para cadastrar nova conta solicitando dados e senha do usuário
  
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
  
  // Método para realizar depósito em uma conta autenticada
  
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
    
    // Requer autenticação antes de permitir depósito
    
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
  
  // Método para realizar saque de uma conta autenticada e com saldo suficiente
  
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
    
    // Autenticação da conta para segurança
    
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
  
  // Método para exibir saldo da conta após autenticação
  
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
    
    // Autenticação para proteger dados da conta
    
    if (!autenticacao(conta)) 
    {
      System.out.println("Senha incorreta. Operacao cancelada.");
      return;
    }
    
    System.out.println("Saldo atual: " + conta.saldo);
  }
  
  // Método auxiliar para buscar uma conta pelo número informado
  
  static Conta buscarConta(int numero) {
    
    for (int i = 0; i < cont; i++) 
    {
      if (cadastro[i].numero == numero) 
      {
        return cadastro[i];
      }
    }
    
    return null; // Retorna null se a conta não for encontrada
  }
  
  // Método para autenticar o usuário solicitando a senha da conta
  
  static boolean autenticacao(Conta conta) {
    
    System.out.print("Digite a senha da conta: ");
    String senhaDigitada = scanner.nextLine();
    return conta.senha.equals(senhaDigitada);
  }
  
  // Método para realizar transferência entre contas após autenticação da conta origem
  
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
    
    // Autentica conta origem para garantir segurança
    
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
    
    // Verifica saldo suficiente para transferência
    
    if (valor > origem.saldo) 
    {
      System.out.println("Saldo insuficiente!");
      return;
    }
    
    // Atualiza saldos das contas envolvidas na transferência
    
    origem.saldo -= valor;
    destino.saldo += valor;
    
    System.out.println("Transferencia realizada com sucesso!");
  }
}
