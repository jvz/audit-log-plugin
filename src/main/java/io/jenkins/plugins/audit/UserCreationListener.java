    /*
     * The MIT License
     *
     * Permission is hereby granted, free of charge, to any person obtaining a copy
     * of this software and associated documentation files (the "Software"), to deal
     * in the Software without restriction, including without limitation the rights
     * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
     * copies of the Software, and to permit persons to whom the Software is
     * furnished to do so, subject to the following conditions:
     *
     * The above copyright notice and this permission notice shall be included in
     * all copies or substantial portions of the Software.
     *
     * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
     * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
     * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
     * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
     * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
     * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
     * THE SOFTWARE.
     */

    package io.jenkins.plugins.audit;

    import hudson.ExtensionList;
    import hudson.ExtensionPoint;

    import java.net.InetAddress;
    import java.time.Instant;

    import org.apache.logging.log4j.audit.event.CreateUser;
    import org.apache.logging.log4j.audit.LogEventFactory;
    import org.apache.logging.log4j.core.util.NetUtils;

    /**
     * Listener notified of user account creation events.
     */
    public abstract class UserCreationListener extends ExtensionPoint {
      
        CreateUser user = LogEventFactory.getEvent(CreateUser.class);
        String hostName = NetUtils.getLocalHostname();
        String inetAddress = InetAddress.getLocalHost().getHostAddress();

        /**
         * Fired when a user account is created.
         * @param username the user
         */
        protected void userCreated(@Nonnull String username) {}

        /**
         * Fired when a user account is created, notifies all listeners.
         * @param username the user
         */
        public static void fireUserCreated(@Nonnull String username) {
            String currentTime = Instant.now().toString();
            RequestContext.setHostName(hostName);
            RequestContext.setIpAddress(inetAddress);
            RequestContext.setTimeStamp()
            RequestContext.setUserId(username);

            user.setUserId(username);
            user.setTimeStamp(currentTime);
            user.logEvent();
            RequestContext.clear();

            for (UserCreationListener u : all()) {
                u.userCreated(username)
            }
        }

        private static List<UserCreationListener> all() {
            return ExtensionList.lookup(UserCreationListener.class);
        }

      
    }
