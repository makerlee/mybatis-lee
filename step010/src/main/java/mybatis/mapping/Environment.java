package mybatis.mapping;

import javax.sql.DataSource;

import mybatis.transaction.TransactionFactory;

/**
 * @Description 环境配置
 * @see <a href=
 *      "https://mybatis.org/mybatis-3/zh/configuration.html#environments">mybatis官方文档</a>
 * @Author jiyang.li
 * @Date 2022/9/20 09:19
 **/
public final class Environment {
	private final String id;
	private final TransactionFactory transactionFactory;
	private final DataSource dataSource;

	public Environment(String id, TransactionFactory transactionFactory, DataSource dataSource) {
		this.id = id;
		this.transactionFactory = transactionFactory;
		this.dataSource = dataSource;
	}

	public static class Builder {
		private String id;
		private TransactionFactory transactionFactory;
		private DataSource dataSource;

		public Builder(String id) {
			this.id = id;
		}

		public Builder transactionFactory(TransactionFactory transactionFactory) {
		    this.transactionFactory = transactionFactory;
		    return this;
        }

        public Builder dataSource(DataSource dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        public Environment build() {
		    return new Environment(id, transactionFactory, dataSource);
        }

		public String id() {
			return id;
		}
	}

	public String getId() {
		return id;
	}

	public TransactionFactory getTransactionFactory() {
		return transactionFactory;
	}

	public DataSource getDataSource() {
		return dataSource;
	}
}
