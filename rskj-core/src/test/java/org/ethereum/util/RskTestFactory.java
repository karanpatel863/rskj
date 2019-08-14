package org.ethereum.util;

import co.rsk.blockchain.utils.BlockGenerator;
import co.rsk.config.RskSystemProperties;
import co.rsk.config.TestSystemProperties;
import co.rsk.core.genesis.TestGenesisLoader;
import co.rsk.db.MutableTrieCache;
import co.rsk.db.MutableTrieImpl;
import co.rsk.net.sync.SyncConfiguration;
import co.rsk.trie.Trie;
import co.rsk.validators.BlockValidator;
import co.rsk.validators.DummyBlockValidator;
import org.ethereum.config.blockchain.upgrades.ConsensusRule;
import org.ethereum.core.Genesis;
import org.ethereum.core.Repository;
import org.ethereum.core.genesis.GenesisLoader;
import org.ethereum.db.MutableRepository;

/**
 * @deprecated use {@link RskTestContext} instead which builds the same graph of dependencies than the productive code
 */
@Deprecated
public class RskTestFactory extends RskTestContext {
    private final TestSystemProperties config;

    public RskTestFactory() {
        this(new TestSystemProperties());
    }

    public RskTestFactory(TestSystemProperties config) {
        super(new String[0]);
        this.config = config;
    }

    @Override
    public RskSystemProperties buildRskSystemProperties() {
        return config;
    }

    @Override
    public BlockValidator buildBlockValidator() {
        return new DummyBlockValidator();
    }

    @Override
    protected GenesisLoader buildGenesisLoader() {
        return () -> {
            Genesis genesis = new BlockGenerator().getGenesisBlock();
            getStateRootHandler().register(genesis.getHeader(), new Trie());
            return genesis;
        };
    }

    @Override
    public SyncConfiguration buildSyncConfiguration() {
        return SyncConfiguration.IMMEDIATE_FOR_TESTING;
    }

    public static Genesis getGenesisInstance(RskSystemProperties config) {
        boolean useRskip92Encoding = config.getActivationConfig().isActive(ConsensusRule.RSKIP92, 0L);
        boolean isRskip126Enabled = config.getActivationConfig().isActive(ConsensusRule.RSKIP126, 0L);
        Repository repository = new MutableRepository(new MutableTrieCache(new MutableTrieImpl(null, new Trie())));
        return new TestGenesisLoader(repository, config.genesisInfo(), config.getNetworkConstants().getInitialNonce(), false, useRskip92Encoding, isRskip126Enabled).load();
    }
}
