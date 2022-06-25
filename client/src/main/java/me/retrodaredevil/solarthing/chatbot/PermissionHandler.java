package me.retrodaredevil.solarthing.chatbot;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public class PermissionHandler {


	private List<String> split(String toSplit) {
		return Stream.of(toSplit.split("\\.")).collect(Collectors.toList());
	}
	private List<Permission> convert(List<String> permissions) {
		return permissions.stream().map(Permission::parse).collect(Collectors.toList());
	}
	/**
	 *
	 * @param userPermissionString The permission the user has such as "solarthing.commands.*" or "solarthing.commands.1.GEN OFF"
	 * @param desiredPermissionString The permission we want to see if the user has
	 */
	public boolean permissionMatches(String userPermissionString, String desiredPermissionString) {
		requireNonNull(userPermissionString);
		requireNonNull(desiredPermissionString);
		List<Permission> userPermissions = convert(split(userPermissionString));
		List<String> desiredPermissions = split(desiredPermissionString);

		for (int userIndex = 0, desiredIndex = 0; true; userIndex++, desiredIndex++) {
			Permission userPermission = userPermissions.size() <= userIndex ? null : userPermissions.get(userIndex);
			String desiredPermission = desiredPermissions.size() <= desiredIndex ? null : desiredPermissions.get(desiredIndex);
			if ((userPermission == null || userPermission.expandedWildcard) && desiredPermission == null) {
				return true;
			}
			if (userPermission == null) {
				// a.b, a.b.c
				return false;
			}
			if (desiredPermission == null) {
				// a.b.c, a.b
				return false;
			}
			if (userPermission.expandedWildcard) {
				Permission nextUser = userPermissions.size() <= userIndex + 1 ? null : userPermissions.get(userIndex + 1);
				if (nextUser == null) {
					// a.b.c.*, a.b.c.d.e
					return true;
				}
				while (true) {
					String nextDesired = desiredPermissions.size() <= desiredIndex ? null : desiredPermissions.get(desiredIndex);
					if (nextDesired == null) {
						// a.b.c.*.e, a.b.c.d
						return false;
					}
					if (nextUser.node.equals(nextDesired)) {
						break;
					}
					desiredIndex++;
				}
				desiredIndex--;
			} else if (!userPermission.singleWildcard && !userPermission.node.equals(desiredPermission)) {
				return false;
			}
		}
	}

	private static class Permission {
		private static final Permission EXPANDED_WILDCARD = new Permission(null, true, false);
		private static final Permission SINGLE_WILDCARD = new Permission(null, false, true);

		private final String node;
		private final boolean expandedWildcard;
		private final boolean singleWildcard;

		private Permission(String node) {
			this(requireNonNull(node), false, false);
		}
		private Permission(String node, boolean expandedWildcard, boolean singleWildcard) {
			this.node = node;
			this.expandedWildcard = expandedWildcard;
			this.singleWildcard = singleWildcard;
		}
		static Permission parse(String node) {
			if ("?".equals(node)) {
				return SINGLE_WILDCARD;
			} else if ("*".equals(node)) {
				return EXPANDED_WILDCARD;
			}
			return new Permission(node);
		}

		@Override
		public String toString() {
			if (expandedWildcard) {
				return "*";
			}
			if (singleWildcard) {
				return "?";
			}
			return "Permission(" + node + ")";
		}
	}
}
