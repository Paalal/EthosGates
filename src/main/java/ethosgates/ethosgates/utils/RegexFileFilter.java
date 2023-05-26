package ethosgates.ethosgates.utils;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Filters files using supplied regular expression(s).
 * <p>
 * See java.util.regex.Pattern for regex matching rules.
 * </p>
 * <h2>Using Classic IO</h2>
 * <p>
 * e.g.
 *
 * <pre>
 * File dir = new File(".");
 * FileFilter fileFilter = new RegexFileFilter("^.*[tT]est(-\\d+)?\\.java$");
 * File[] files = dir.listFiles(fileFilter);
 * for (String file : files) {
 *     System.out.println(file);
 * }
 * </pre>
 *
 * <h2>Using NIO</h2>
 *
 * <pre>
 * final Path dir = Paths.get("");
 * final AccumulatorPathVisitor visitor = AccumulatorPathVisitor.withLongCounters(new RegexFileFilter("^.*[tT]est(-\\d+)?\\.java$"));
 * //
 * // Walk one dir
 * Files.<b>walkFileTree</b>(dir, Collections.emptySet(), 1, visitor);
 * System.out.println(visitor.getPathCounters());
 * System.out.println(visitor.getFileList());
 * //
 * visitor.getPathCounters().reset();
 * //
 * // Walk dir tree
 * Files.<b>walkFileTree</b>(dir, visitor);
 * System.out.println(visitor.getPathCounters());
 * System.out.println(visitor.getDirList());
 * System.out.println(visitor.getFileList());
 * </pre>
 *
 * @since 1.4
 */
public class RegexFileFilter implements FileFilter {

    private static final long serialVersionUID = 4269646126155225L;

    /**
     * Compiles the given pattern source.
     *
     * @param pattern the source pattern.
     * @param flags the compilation flags.
     * @return a new Pattern.
     */
    private static Pattern compile(final String pattern, final int flags) {
        if (pattern == null) {
            throw new IllegalArgumentException("Pattern is missing");
        }
        return Pattern.compile(pattern, flags);
    }

    /** The regular expression pattern that will be used to match file names. */
    private final Pattern pattern;

    /** How convert a path to a string. */
    private final Function<Path, String> pathToString;

    /**
     * Constructs a new regular expression filter for a compiled regular expression
     *
     * @param pattern regular expression to match.
     * @throws IllegalArgumentException if the pattern is null.
     */
    public RegexFileFilter(final Pattern pattern) {
        this(pattern, p -> p.getFileName().toString());
    }

    /**
     * Constructs a new regular expression filter for a compiled regular expression
     *
     * @param pattern regular expression to match.
     * @param pathToString How convert a path to a string.
     * @throws IllegalArgumentException if the pattern is null.
     * @since 2.10.0
     */
    public RegexFileFilter(final Pattern pattern, final Function<Path, String> pathToString) {
        if (pattern == null) {
            throw new IllegalArgumentException("Pattern is missing");
        }
        this.pattern = pattern;
        this.pathToString = pathToString;
    }

    /**
     * Constructs a new regular expression filter.
     *
     * @param pattern regular string expression to match
     * @throws IllegalArgumentException if the pattern is null
     */
    public RegexFileFilter(final String pattern) {
        this(pattern, 0);
    }

    /**
     * Constructs a new regular expression filter with the specified flags.
     *
     * @param pattern regular string expression to match
     * @param flags pattern flags - e.g. {@link Pattern#CASE_INSENSITIVE}
     * @throws IllegalArgumentException if the pattern is null
     */
    public RegexFileFilter(final String pattern, final int flags) {
        this(compile(pattern, flags));
    }

    /**
     * Checks to see if the file name matches one of the regular expressions.
     *
     * @param dir the file directory (ignored)
     * @param name the file name
     * @return true if the file name matches one of the regular expressions
     */
    public boolean accept(final File dir, final String name) {
        return pattern.matcher(name).matches();
    }


    /**
     * Returns a debug string.
     *
     * @since 2.10.0
     */
    @Override
    public String toString() {
        return "RegexFileFilter [pattern=" + pattern + "]";
    }

    @Override
    public boolean accept(File pathname) {
        return false;
    }
}
