/*
 * This file is part of RskJ
 * Copyright (C) 2017 RSK Labs Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package co.rsk.net.handler.txvalidator;

import co.rsk.core.Coin;
import co.rsk.net.TransactionValidationResult;
import org.ethereum.config.Constants;
import org.ethereum.core.AccountState;
import org.ethereum.core.Transaction;

import javax.annotation.Nullable;
import java.math.BigInteger;

/**
 * Checks that the transaction gas limit is lower than the `block` gas limit
 * though there's no check that the actual block gas limit is used
 * Also Checks that the transaction gas limit is not higher than the max allowed value
 */
public class TxValidatorGasLimitValidator implements TxValidatorStep {
    @Override
    public TransactionValidationResult validate(Transaction tx, @Nullable AccountState state, BigInteger gasLimit, Coin minimumGasPrice, long bestBlockNumber, boolean isFreeTx) {
        BigInteger txGasLimit = tx.getGasLimitAsInteger();

        if (txGasLimit.compareTo(gasLimit) <= 0 && txGasLimit.compareTo(Constants.getTransactionGasCap()) <= 0) {
            return TransactionValidationResult.ok();
        }

        return TransactionValidationResult.withError(String.format(
                "transaction's gas limit of %s is higher than the block's gas limit of %s",
                txGasLimit,
                gasLimit
        ));
    }

}
