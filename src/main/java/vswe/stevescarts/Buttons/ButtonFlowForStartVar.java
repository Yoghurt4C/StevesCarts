package vswe.stevescarts.Buttons;

import vswe.stevescarts.Computer.ComputerTask;
import vswe.stevescarts.Modules.Workers.ModuleComputer;

public class ButtonFlowForStartVar extends ButtonFlowForVar {
	public ButtonFlowForStartVar(final ModuleComputer module, final LOCATION loc, final boolean increase) {
		super(module, loc, increase);
	}

	@Override
	protected int getIndex(final ComputerTask task) {
		return task.getFlowForStartVarIndex();
	}

	@Override
	protected void setIndex(final ComputerTask task, final int val) {
		task.setFlowForStartVar(val);
	}

	@Override
	protected String getName() {
		return "start";
	}

	@Override
	protected boolean isVarVisible(final ComputerTask task) {
		return task.getFlowForUseStartVar();
	}
}
