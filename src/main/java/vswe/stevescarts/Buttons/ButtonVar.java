package vswe.stevescarts.Buttons;

import vswe.stevescarts.Computer.ComputerTask;
import vswe.stevescarts.Modules.Workers.ModuleComputer;

public abstract class ButtonVar extends ButtonAssembly {
	public ButtonVar(final ModuleComputer module, final LOCATION loc) {
		super(module, loc);
	}

	@Override
	public boolean isVisible() {
		if (!super.isVisible()) {
			return false;
		}
		if (((ModuleComputer) this.module).getSelectedTasks() != null && ((ModuleComputer) this.module).getSelectedTasks().size() > 0) {
			for (final ComputerTask task : ((ModuleComputer) this.module).getSelectedTasks()) {
				if (!ComputerTask.isVar(task.getType()) || task.isVarEmpty() || (this.isSecondValue() && !task.hasTwoValues())) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	protected boolean isSecondValue() {
		return false;
	}
}
