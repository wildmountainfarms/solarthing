package me.retrodaredevil.solarthing.commands.sequence.condition;

public class TimedCondition implements Condition {
	private final long waitTime;
	private final TimeProvider timeProvider;
	
	public TimedCondition(long waitTime, TimeProvider timeProvider) {
		this.waitTime = waitTime;
		this.timeProvider = timeProvider;
	}
	public TimedCondition(long waitTime){
		this(waitTime, System::currentTimeMillis);
	}
	
	@Override
	public ConditionTask start() {
		return new TimedConditionTask();
	}
	
	public interface TimeProvider {
		long getTimeMillis();
	}
	public class TimedConditionTask implements ConditionTask {
		private final long endTime;
		private TimedConditionTask(){
			endTime = timeProvider.getTimeMillis() + waitTime;
		}
		
		@Override
		public boolean isDone() {
			return endTime <= timeProvider.getTimeMillis();
		}
	}
}
