package com.sumridge.pl.processor;

import java.util.HashMap;
import java.util.Map;

import com.sumridge.pl.util.Constant.Signal;

public class PlRefreshProcessorFactory {
	static Map<Signal, Class> states = new HashMap<Signal, Class>();
	static {
		states.put(Signal.Position, PositionProcessor.class);
		states.put(Signal.Price, PriceProcessor.class);
		states.put(Signal.Setup, SetupProcessor.class);
		states.put(Signal.Total, TotalProcessor.class);
		states.put(Signal.Trade, TradeProcessor.class);
		states.put(Signal.MSD, MsdProcessor.class);
	}

	public static IPlRefreshProcessor createPlugin(Signal key) throws Exception {
		return (IPlRefreshProcessor) states.get(key).newInstance();
	}
}
