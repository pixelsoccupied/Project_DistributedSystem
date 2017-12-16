package InnaClient;

public class Configuration {
	
	public class CORBA {
        public static final String ROOT_POA = "RootPOA";
        public static final String NAME_SERVICE = "NameService";
    }

    
    // BRANCH SERVERS
    public enum BRANCH_ID {BC, MB, NB, QC}
    
    public static final String BC_HOSTNAME = "localhost";
    public static final String MB_HOSTNAME = "localhost";
    public static final String NB_HOSTNAME = "localhost";
    public static final String QC_HOSTNAME = "localhost";
    
    // UDP PORTS
    public static final int BC_UDP_PORT = 4964;
    public static final int MB_UDP_PORT = 4965;
    public static final int NB_UDP_PORT = 4966;
    public static final int QC_UDP_PORT = 4967;
    
    //FUNCTIONS
    public static final String FUNCION_GET_ACCOUNT_NUMBER = "getAccountNumber";
    
    public static final String FUNCION_TRANSFER_FUND_DEPOSIT = "transferFundDeposit";
    public static final String FUNCION_TRANSFER_FUND_WITHDRAW = "transferFundWithdraw";
    
    
    public static final String FUNCION_VERIFY_SOURCE_CUSTOMER = "verifySourceCustomer";
    public static final String FUNCION_VERIFY_DESTINATION_CUSTOMER = "verifyDestinationCustomer";
    public static final String FUNCION_VERIFY_FUNDS = "verifyFunds";
    
    public static final String DELIMITER = "\\|";
  
    public static String getHostnameByBranchID(BRANCH_ID branch) {
        switch (branch) {
            case BC:
                return BC_HOSTNAME;
            case MB:
                return MB_HOSTNAME;
            case NB:
                return NB_HOSTNAME;
            case QC:
                return QC_HOSTNAME;
            default:
                return "Wrong Server ID";
        }
    }
    
    public static int getUDPPortByBranchID(BRANCH_ID branch) {
        switch (branch) {
            case BC:
                return BC_UDP_PORT;
            case MB:
                return MB_UDP_PORT;
            case NB:
                return NB_UDP_PORT;
            case QC:
                return QC_UDP_PORT;
            default:
                return 0;
        }
    }

}
