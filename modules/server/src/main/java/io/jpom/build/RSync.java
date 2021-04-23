
package io.jpom.build;

import cn.hutool.core.io.FileUtil;
import cn.hutool.system.SystemUtil;
import cn.jiangzeyin.common.DefaultSystemLog;
import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;


/**
 * RSYNC类
 * @author myzf
 * @date 2021/4/20
 */
public class RSync {


  /**
   *
   * rsync参数的具体解释如下： 
   *
   * -v, --verbose 详细模式输出 
   *
   * -q, --quiet 精简输出模式 
   *
   * -c, --checksum 打开校验开关，强制对文件传输进行校验 
   *
   * -a, --archive 归档模式，表示以递归方式传输文件，并保持所有文件属性，等于-rlptgoD 
   *
   * -r, --recursive 对子目录以递归模式处理 
   *
   * -R, --relative 使用相对路径信息 
   *
   * -b, --backup 创建备份，也就是对于目的已经存在有同样的文件名时，将老的文件重新命名为~filename。可以使用--suffix选项来指定不同的备份文件前缀。 
   *
   * --backup-dir 将备份文件(如~filename)存放在在目录下。 
   *
   * -suffix=SUFFIX 定义备份文件前缀 
   *
   * -u, --update 仅仅进行更新，也就是跳过所有已经存在于DST，并且文件时间晚于要备份的文件。(不覆盖更新的文件) 
   *
   * -l, --links 保留软链结 
   *
   * -L, --copy-links 想对待常规文件一样处理软链结 
   *
   * --copy-unsafe-links 仅仅拷贝指向SRC路径目录树以外的链结 
   *
   * --safe-links 忽略指向SRC路径目录树以外的链结 
   *
   * -H, --hard-links 保留硬链结 
   *
   * -p, --perms 保持文件权限 
   *
   * -o, --owner 保持文件属主信息 
   *
   * -g, --group 保持文件属组信息 
   *
   * -D, --devices 保持设备文件信息 
   *
   * -t, --times 保持文件时间信息 
   *
   * -S, --sparse 对稀疏文件进行特殊处理以节省DST的空间 
   *
   * -n, --dry-run现实哪些文件将被传输 
   *
   * -W, --whole-file 拷贝文件，不进行增量检测 
   *
   * -x, --one-file-system 不要跨越文件系统边界 
   *
   * -B, --block-size=SIZE 检验算法使用的块尺寸，默认是700字节 
   *
   * -e, --rsh=COMMAND 指定使用rsh、ssh方式进行数据同步 
   *
   * --rsync-path=PATH 指定远程服务器上的rsync命令所在路径信息 
   *
   * -C, --cvs-exclude 使用和CVS一样的方法自动忽略文件，用来排除那些不希望传输的文件 
   *
   * --existing 仅仅更新那些已经存在于DST的文件，而不备份那些新创建的文件 
   *
   * --delete 删除那些DST中SRC没有的文件 
   *
   * --delete-excluded 同样删除接收端那些被该选项指定排除的文件 
   *
   * --delete-after 传输结束以后再删除 
   *
   * --ignore-errors 及时出现IO错误也进行删除 
   *
   * --max-delete=NUM 最多删除NUM个文件 
   *
   * --partial 保留那些因故没有完全传输的文件，以是加快随后的再次传输 
   *
   * --force 强制删除目录，即使不为空 
   *
   * --numeric-ids 不将数字的用户和组ID匹配为用户名和组名 
   *
   * --timeout=TIME IP超时时间，单位为秒 
   *
   * -I, --ignore-times 不跳过那些有同样的时间和长度的文件 
   *
   * --size-only 当决定是否要备份文件时，仅仅察看文件大小而不考虑文件时间 
   *
   * --modify-window=NUM 决定文件是否时间相同时使用的时间戳窗口，默认为0 
   *
   * -T --temp-dir=DIR 在DIR中创建临时文件 
   *
   * --compare-dest=DIR 同样比较DIR中的文件来决定是否需要备份 
   *
   * -P 等同于 --partial 
   *
   * --progress 显示备份过程 
   *
   * -z, --compress 对备份的文件在传输时进行压缩处理 
   *
   * --exclude=PATTERN 指定排除不需要传输的文件模式 
   *
   * --include=PATTERN 指定不排除而需要传输的文件模式 
   *
   * --exclude-from=FILE 排除FILE中指定模式的文件 
   *
   * --include-from=FILE 不排除FILE指定模式匹配的文件 
   *
   * --version 打印版本信息 
   *
   * --address 绑定到特定的地址 
   *
   * --config=FILE 指定其他的配置文件，不使用默认的rsyncd.conf文件 
   *
   * --port=PORT 指定其他的rsync服务端口 
   *
   * --blocking-io 对远程shell使用阻塞IO 
   *
   * -stats 给出某些文件的传输状态 
   *
   * --progress 在传输时现实传输过程 
   *
   * --log-format=formAT 指定日志文件格式 
   *
   * --password-file=FILE 从FILE中得到密码 
   *
   * --bwlimit=KBPS 限制I/O带宽，KBytes per second 
   *
   * -h, --help 显示帮助信息 
   *
   *
   * */

  public RSync (){
    reset();
  }

  /** the rsync binary. */
  protected static String rsyncBinary;

  /** whether to output the commandline. */
  protected boolean outputCommandline;

  /** the source path/url. */
  protected List<String> sources;

  /** the destination path/url. */
  protected String destination;

  protected boolean verbose;

  protected String info;

  protected String debug;

  protected boolean msgs2stderr;

  protected boolean quiet;

  protected boolean no_motd;

  protected boolean checksum;

  protected boolean archive;

  protected boolean recursive;

  protected boolean relative;

  protected boolean no_implied_dirs;

  protected boolean backup;

  protected String backup_dir;

  protected String suffix;

  protected boolean update;

  protected boolean inplace;

  protected boolean append;

  protected boolean append_verify;

  protected boolean dirs;

  protected boolean links;

  protected boolean copy_links;

  protected boolean copy_unsafe_links;

  protected boolean safe_links;

  protected boolean munge_links;

  protected boolean copy_dirlinks;

  protected boolean keep_dirlinks;

  protected boolean hard_links;

  protected boolean perms;

  protected boolean executability;

  protected String chmod;

  protected boolean acls;

  protected boolean xattrs;

  protected boolean owner;

  protected boolean group;

  protected boolean devices;

  protected boolean specials;

  protected boolean times;

  protected boolean omit_dir_times;

  protected boolean omit_link_times;

  protected boolean super_;

  protected boolean fake_super;

  protected boolean sparse;

  protected boolean preallocate;

  protected boolean dry_run;

  protected boolean whole_file;

  protected boolean one_file_system;

  protected String block_size;

  protected String rsh;

  protected String rsync_path;

  protected boolean existing;

  protected boolean ignore_existing;

  protected boolean remove_source_files;

  protected boolean delete;

  protected boolean delete_before;

  protected boolean delete_during;

  protected boolean delete_delay;

  protected boolean delete_after;

  protected boolean delete_excluded;

  protected boolean ignore_missing_args;

  protected boolean delete_missing_args;

  protected boolean ignore_errors;

  protected boolean force;

  protected int max_delete;

  protected String max_size;

  protected String min_size;

  protected boolean partial;

  protected String partial_dir;

  protected boolean delay_updates;

  protected boolean prune_empty_dirs;

  protected boolean numeric_ids;

  protected String usermap;

  protected String groupmap;

  protected String chown;

  protected int timeout;

  protected int contimeout;

  protected boolean ignore_times;

  protected String remote_option;

  protected boolean size_only;

  protected int modify_window;

  protected String temp_dir;

  protected boolean fuzzy;

  protected String[] compare_dest;

  protected String[] copy_dest;

  protected String[] link_dest;

  protected boolean compress;

  protected int compress_level;

  protected String skip_compress;

  protected boolean cvs_exclude;

  protected List<String> include_exclude;

  protected String files_from;

  protected boolean from0;

  protected boolean protect_args;

  protected String address;

  protected int port;

  protected String sockopts;

  protected boolean blocking_io;

  protected boolean stats;

  protected boolean eight_bit_output;

  protected boolean human_readable;

  protected boolean progress;

  protected boolean itemize_changes;

  protected String out_format;

  protected String log_file;

  protected String log_file_format;

  protected String password_file;

  protected boolean list_only;

  protected String bwlimit;

  protected char outbuf;

  protected String write_batch;

  protected String only_write_batch;

  protected String read_batch;

  protected int protocol;

  protected String iconv;

  protected int checksum_seed;

  protected boolean ipv4;

  protected boolean ipv6;

  protected boolean version;

  protected String[] additional;


  public void reset() {
    outputCommandline = false;
    sources = new ArrayList<>();
    destination = null;
    verbose = false;
    info = "";
    debug = "";
    msgs2stderr = false;
    quiet = false;
    no_motd = false;
    checksum = false;
    archive = false;
    recursive = false;
    relative = false;
    no_implied_dirs = false;
    backup = false;
    backup_dir = "";
    suffix = "";
    update = false;
    inplace = false;
    append = false;
    append_verify = false;
    dirs = false;
    links = false;
    copy_links = false;
    copy_unsafe_links = false;
    safe_links = false;
    munge_links = false;
    copy_dirlinks = false;
    keep_dirlinks = false;
    hard_links = false;
    perms = false;
    executability = false;
    chmod = "";
    acls = false;
    xattrs = false;
    owner = false;
    group = false;
    devices = false;
    specials = false;
    times = false;
    omit_dir_times = false;
    omit_link_times = false;
    super_ = false;
    fake_super = false;
    sparse = false;
    preallocate = false;
    dry_run = false;
    whole_file = false;
    one_file_system = false;
    block_size = "";
    rsh = "";
    rsync_path = "";
    existing = false;
    ignore_existing = false;
    remove_source_files = false;
    delete = false;
    delete_before = false;
    delete_during = false;
    delete_delay = false;
    delete_after = false;
    delete_excluded = false;
    ignore_missing_args = false;
    delete_missing_args = false;
    ignore_errors = false;
    force = false;
    max_delete = -1;
    max_size = "";
    min_size = "";
    partial = false;
    partial_dir = "";
    delay_updates = false;
    prune_empty_dirs = false;
    numeric_ids = false;
    usermap = "";
    groupmap = "";
    chown = "";
    timeout = -1;
    contimeout = -1;
    ignore_times = false;
    remote_option = "";
    size_only = false;
    modify_window = -1;
    temp_dir = "";
    fuzzy = false;
    compare_dest = new String[0];
    copy_dest = new String[0];
    link_dest = new String[0];
    compress = false;
    compress_level = -1;
    skip_compress = "";
    cvs_exclude = false;
    include_exclude = new ArrayList<>();
    files_from = "";
    from0 = false;
    protect_args = false;
    address = "";
    port = -1;
    sockopts = "";
    blocking_io = false;
    stats = false;
    eight_bit_output = false;
    human_readable = false;
    progress = false;
    progress = false;
    itemize_changes = false;
    out_format = "";
    log_file = "";
    log_file_format = "";
    password_file = "";
    list_only = false;
    bwlimit = "";
    outbuf = '\0';
    write_batch = "";
    only_write_batch = "";
    read_batch = "";
    protocol = -1;
    iconv = "";
    checksum_seed = -1;
    ipv4 = false;
    ipv6 = false;
    version = false;
  }


  public RSync source(String value) {
    sources.clear();
    sources.add(convertPath(value));
    return this;
  }



  public RSync sources(String[] value) {
    sources.clear();
    for (String s: value)
      sources.add(convertPath(s));
    return this;
  }


  public RSync sources(List<String> value) {
    sources.clear();
    for (String s: value)
      sources.add(convertPath(s));
    return this;
  }


  public List<String> getSources() {
    return sources;
  }


  public RSync destination(String value) {
    destination = convertPath(value);
    return this;
  }


  public String getDestination() {
    return destination;
  }
  


  public boolean isVerbose() {
    return verbose;
  }


  public RSync verbose(boolean verbose) {
    this.verbose = verbose;
    return this;
  }


  public String getInfo() {
    return info;
  }


  public RSync info(String info) {
    this.info = info;
    return this;
  }


  public String getDebug() {
    return debug;
  }
  


  public boolean isMsgs2stderr() {
    return msgs2stderr;
  }



  public boolean isQuiet() {
    return quiet;
  }


  public RSync quiet(boolean quiet) {
    this.quiet = quiet;
    return this;
  }


  public boolean isNoMotd() {
    return no_motd;
  }


  public RSync noMotd(boolean no_motd) {
    this.no_motd = no_motd;
    return this;
  }


  public boolean isChecksum() {
    return checksum;
  }



  public boolean isArchive() {
    return archive;
  }



  public boolean isRecursive() {
    return recursive;
  }


  public RSync recursive(boolean recursive) {
    this.recursive = recursive;
    return this;
  }


  public boolean isRelative() {
    return relative;
  }


  public RSync relative(boolean relative) {
    this.relative = relative;
    return this;
  }


  public boolean isNoImpliedDirs() {
    return no_implied_dirs;
  }



  public boolean isBackup() {
    return backup;
  }




  public String getBackupDir() {
    return backup_dir;
  }



  public String getSuffix() {
    return suffix;
  }




  public boolean isUpdate() {
    return update;
  }


  public RSync update(boolean update) {
    this.update = update;
    return this;
  }


  public boolean isInplace() {
    return inplace;
  }




  public boolean isAppend() {
    return append;
  }


  public RSync append(boolean append) {
    this.append = append;
    return this;
  }


  public boolean isAppendVerify() {
    return append_verify;
  }



  public boolean isDirs() {
    return dirs;
  }


  public RSync dirs(boolean dirs) {
    this.dirs = dirs;
    return this;
  }


  public boolean isLinks() {
    return links;
  }


  public RSync links(boolean links) {
    this.links = links;
    return this;
  }


  public boolean isCopyLinks() {
    return copy_links;
  }




  public boolean isCopyUnsafeLinks() {
    return copy_unsafe_links;
  }



  public boolean isSafeLinks() {
    return safe_links;
  }


  public boolean isMungeLinks() {
    return munge_links;
  }



  public boolean isCopyDirlinks() {
    return copy_dirlinks;
  }




  public boolean isKeepDirlinks() {
    return keep_dirlinks;
  }




  public boolean isHardLinks() {
    return hard_links;
  }



  public boolean isPerms() {
    return perms;
  }



  public boolean isExecutability() {
    return executability;
  }



  public String getChmod() {
    return chmod;
  }



  public boolean isAcls() {
    return acls;
  }



  public boolean isXattrs() {
    return xattrs;
  }

 

  public boolean isOwner() {
    return owner;
  }

  

  public boolean isGroup() {
    return group;
  }


  public RSync group(boolean group) {
    this.group = group;
    return this;
  }


  public boolean isDevices() {
    return devices;
  }




  public boolean isSpecials() {
    return specials;
  }




  public boolean isTimes() {
    return times;
  }


  public RSync times(boolean times) {
    this.times = times;
    return this;
  }


  public boolean isOmitDirTimes() {
    return omit_dir_times;
  }


  public boolean isOmitLinkTimes() {
    return omit_link_times;
  }

  


  public boolean isSuper_() {
    return super_;
  }

 


  public boolean isFakeSuper() {
    return fake_super;
  }

 


  public boolean isSparse() {
    return sparse;
  }



  public boolean isPreallocate() {
    return preallocate;
  }

  


  public boolean isDryRun() {
    return dry_run;
  }

 


  public boolean isWholeFile() {
    return whole_file;
  }

  


  public boolean isOneFileSystem() {
    return one_file_system;
  }

 


  public String getBlockSize() {
    return block_size;
  }




  public String getRsh() {
    return rsh;
  }




  public String getRsyncPath() {
    return rsync_path;
  }


  public boolean isExisting() {
    return existing;
  }


  public RSync existing(boolean existing) {
    this.existing = existing;
    return this;
  }


  public boolean isIgnoreExisting() {
    return ignore_existing;
  }


  public RSync ignoreExisting(boolean ignore_existing) {
    this.ignore_existing = ignore_existing;
    return this;
  }


  public boolean isRemoveSourceFiles() {
    return remove_source_files;
  }

  public RSync removeSourceFiles(boolean remove_source_files) {
    this.remove_source_files = remove_source_files;
    return this;
  }


  public boolean isDelete() {
    return delete;
  }


  public RSync delete(boolean delete) {
    this.delete = delete;
    return this;
  }


  public boolean isDeleteBefore() {
    return delete_before;
  }


  public RSync deleteBefore(boolean delete_before) {
    this.delete_before = delete_before;
    return this;
  }


  public boolean isDeleteDuring() {
    return delete_during;
  }


  public RSync deleteDuring(boolean delete_during) {
    this.delete_during = delete_during;
    return this;
  }


  public boolean isDeleteDelay() {
    return delete_delay;
  }


  public RSync deleteDelay(boolean delete_delay) {
    this.delete_delay = delete_delay;
    return this;
  }


  public boolean isDeleteAfter() {
    return delete_after;
  }


  public RSync deleteAfter(boolean delete_after) {
    this.delete_after = delete_after;
    return this;
  }


  public boolean isDeleteExcluded() {
    return delete_excluded;
  }


  public RSync deleteExcluded(boolean delete_excluded) {
    this.delete_excluded = delete_excluded;
    return this;
  }


  public boolean isIgnoreMissingArgs() {
    return ignore_missing_args;
  }


  public RSync ignoreMissingArgs(boolean ignore_missing_args) {
    this.ignore_missing_args = ignore_missing_args;
    return this;
  }


  public boolean isDeleteMissingArgs() {
    return delete_missing_args;
  }


  public RSync deleteMissingArgs(boolean delete_missing_args) {
    this.delete_missing_args = delete_missing_args;
    return this;
  }


  public boolean isIgnoreErrors() {
    return ignore_errors;
  }


  public RSync ignoreErrors(boolean ignore_errors) {
    this.ignore_errors = ignore_errors;
    return this;
  }


  public boolean isForce() {
    return force;
  }


  public RSync force(boolean force) {
    this.force = force;
    return this;
  }


  public int getMaxDelete() {
    return max_delete;
  }


  public RSync maxDelete(int max_delete) {
    this.max_delete = max_delete;
    return this;
  }


  public String getMaxSize() {
    return max_size;
  }


  public RSync maxSize(String max_size) {
    this.max_size = max_size;
    return this;
  }


  public String getMinSize() {
    return min_size;
  }


  public RSync minSize(String min_size) {
    this.min_size = min_size;
    return this;
  }


  public boolean isPartial() {
    return partial;
  }


  public RSync partial(boolean partial) {
    this.partial = partial;
    return this;
  }


  public String getPartialDir() {
    return partial_dir;
  }

  public RSync partialDir(String partial_dir) {
    this.partial_dir = partial_dir;
    return this;
  }

  public boolean isDelayUpdates() {
    return delay_updates;
  }


  public RSync delayUpdates(boolean delay_updates) {
    this.delay_updates = delay_updates;
    return this;
  }


  public boolean isPruneEmptyDirs() {
    return prune_empty_dirs;
  }


  public RSync pruneEmptyDirs(boolean prune_empty_dirs) {
    this.prune_empty_dirs = prune_empty_dirs;
    return this;
  }


  public boolean isNumericIds() {
    return numeric_ids;
  }


  public RSync numericIds(boolean numeric_ids) {
    this.numeric_ids = numeric_ids;
    return this;
  }


  public String getUsermap() {
    return usermap;
  }


  public RSync usermap(String usermap) {
    this.usermap = usermap;
    return this;
  }


  public String getGroupmap() {
    return groupmap;
  }


  public RSync groupmap(String groupmap) {
    this.groupmap = groupmap;
    return this;
  }


  public String getChown() {
    return chown;
  }


  public RSync chown(String chown) {
    this.chown = chown;
    return this;
  }


  public int getTimeout() {
    return timeout;
  }


  public RSync timeout(int timeout) {
    this.timeout = timeout;
    return this;
  }


  public int getContimeout() {
    return contimeout;
  }


  public RSync contimeout(int contimeout) {
    this.contimeout = contimeout;
    return this;
  }


  public boolean isIgnoreTimes() {
    return ignore_times;
  }


  public RSync ignoreTimes(boolean ignore_times) {
    this.ignore_times = ignore_times;
    return this;
  }


  public String getRemoteOption() {
    return remote_option;
  }


  public RSync remoteOption(String remote_option) {
    this.remote_option = remote_option;
    return this;
  }


  public boolean isSizeOnly() {
    return size_only;
  }


  public RSync sizeOnly(boolean size_only) {
    this.size_only = size_only;
    return this;
  }


  public int getModifyWindow() {
    return modify_window;
  }


  public RSync modifyWindow(int modify_window) {
    this.modify_window = modify_window;
    return this;
  }


  public String getTempDir() {
    return temp_dir;
  }


  public RSync tempDir(String temp_dir) {
    this.temp_dir = temp_dir;
    return this;
  }


  public boolean isFuzzy() {
    return fuzzy;
  }


  public RSync fuzzy(boolean fuzzy) {
    this.fuzzy = fuzzy;
    return this;
  }


  public String[] getCompareDest() {
    return compare_dest;
  }


  public RSync compareDest(String... compare_dest) {
    this.compare_dest = compare_dest.clone();
    return this;
  }


  public String[] getCopyDest() {
    return copy_dest;
  }


  public RSync copyDest(String... copy_dest) {
    this.copy_dest = copy_dest.clone();
    return this;
  }


  public String[] getLinkDest() {
    return link_dest;
  }


  public RSync linkDest(String... link_dest) {
    this.link_dest = link_dest.clone();
    return this;
  }


  public boolean isCompress() {
    return compress;
  }


  public RSync compress(boolean compress) {
    this.compress = compress;
    return this;
  }


  public int getCompressLevel() {
    return compress_level;
  }


  public RSync compressLevel(int compress_level) {
    this.compress_level = compress_level;
    return this;
  }


  public String getSkipCompress() {
    return skip_compress;
  }


  public RSync skipCompress(String skip_compress) {
    this.skip_compress = skip_compress;
    return this;
  }


  public boolean isCvsExclude() {
    return cvs_exclude;
  }


  public RSync cvsExclude(boolean cvs_exclude) {
    this.cvs_exclude = cvs_exclude;
    return this;
  }


  public String[] getFilter() {
    return includeExcludeSubset("F");
  }


  public RSync filter(String... filter) {
    return addIncludeExclude("F", filter);
  }


  protected String[] includeExcludeSubset(String id) {
    List<String> result = new ArrayList<>();
    id = id + "\t";
    for (String ie: include_exclude) {
      if (ie.startsWith(id))
        result.add(ie.substring(id.length()));
    }
    return result.toArray(new String[0]);
  }


  protected RSync addIncludeExclude(String id, String... list) {
    for (String l: list)
      this.include_exclude.add(id + "\t" + l);
    return this;
  }

  protected String[] getIncludeExclude() {
    return include_exclude.toArray(new String[0]);
  }


  public String[] getExclude() {
    return includeExcludeSubset("E");
  }


  public RSync exclude(String... exclude) {
    return addIncludeExclude("E", exclude);
  }


  public String[] getExcludeFrom() {
    return includeExcludeSubset("EF");
  }


  public RSync excludeFrom(String... exclude_from) {
    for (int i = 0; i < exclude_from.length; i++)
      exclude_from[i] = convertPath(exclude_from[i]);
    return addIncludeExclude("EF", exclude_from);
  }


  public String[] getInclude() {
    return includeExcludeSubset("I");
  }


  public RSync include(String... include) {
    return addIncludeExclude("I", include);
  }


  public String[] getIncludeFrom() {
    return includeExcludeSubset("IF");
  }


  public RSync includeFrom(String... include_from) {
    for (int i = 0; i < include_from.length; i++)
      include_from[i] = convertPath(include_from[i]);
    return addIncludeExclude("IF", include_from);
  }


  public String getFilesFrom() {
    return files_from;
  }


  public RSync filesFrom(String files_from) {
    this.files_from = convertPath(files_from);
    return this;
  }


  public boolean isFrom0() {
    return from0;
  }


  public RSync from0(boolean from0) {
    this.from0 = from0;
    return this;
  }


  public boolean isProtectArgs() {
    return protect_args;
  }


  public RSync protectArgs(boolean protect_args) {
    this.protect_args = protect_args;
    return this;
  }


  public String getAddress() {
    return address;
  }


  public RSync address(String address) {
    this.address = address;
    return this;
  }


  public int getPort() {
    return port;
  }


  public RSync port(int port) {
    this.port = port;
    return this;
  }


  public String getSockopts() {
    return sockopts;
  }


  public RSync sockopts(String sockopts) {
    this.sockopts = sockopts;
    return this;
  }


  public boolean isBlockingIO() {
    return blocking_io;
  }


  public RSync blockingIO(boolean blocking_io) {
    this.blocking_io = blocking_io;
    return this;
  }


  public boolean isStats() {
    return stats;
  }


  public RSync stats(boolean stats) {
    this.stats = stats;
    return this;
  }


  public boolean isEightBitOutput() {
    return eight_bit_output;
  }


  public RSync eightBitOutput(boolean eight_bit_output) {
    this.eight_bit_output = eight_bit_output;
    return this;
  }

  public boolean isHumanReadable() {
    return human_readable;
  }


  public RSync humanReadable(boolean human_readable) {
    this.human_readable = human_readable;
    return this;
  }


  public boolean isProgress() {
    return progress;
  }


  public RSync progress(boolean progress) {
    this.progress = progress;
    return this;
  }


  public boolean isItemizeChanges() {
    return itemize_changes;
  }



  public String getOutFormat() {
    return out_format;
  }


  public RSync outFormat(String out_format) {
    this.out_format = out_format;
    return this;
  }


  public String getLogFile() {
    return log_file;
  }




  public String getLogFileFormat() {
    return log_file_format;
  }


  public RSync logFileFormat(String log_file_format) {
    this.log_file_format = log_file_format;
    return this;
  }


  public String getPasswordFile() {
    return password_file;
  }


  public boolean isListOnly() {
    return list_only;
  }




  public String getBwlimit() {
    return bwlimit;
  }




  public char getOutbuf() {
    return outbuf;
  }




  public String getWriteBatch() {
    return write_batch;
  }



  public String getOnlyWriteBatch() {
    return only_write_batch;
  }




  public String getReadBatch() {
    return read_batch;
  }




  public int getProtocol() {
    return protocol;
  }

  public RSync protocol(int protocol) {
    this.protocol = protocol;
    return this;
  }


  public String getIconv() {
    return iconv;
  }


  public RSync iconv(String iconv) {
    this.iconv = iconv;
    return this;
  }


  public int getChecksumSeed() {
    return checksum_seed;
  }


  public RSync checksumSeed(int checksum_seed) {
    this.checksum_seed = checksum_seed;
    return this;
  }


  public boolean isIpv4() {
    return ipv4;
  }


  public RSync ipv4(boolean ipv4) {
    this.ipv4 = ipv4;
    return this;
  }


  public boolean isIpv6() {
    return ipv6;
  }


  public RSync ipv6(boolean ipv6) {
    this.ipv6 = ipv6;
    return this;
  }


  public boolean isVersion() {
    return version;
  }


  public RSync version(boolean version) {
    this.version = version;
    return this;
  }

  public String[] getAdditional() {
    return additional;
  }


  public RSync additional(String... additional) {
    this.additional = additional.clone();
    for (int i = 0; i < this.additional.length; i++)
      this.additional[i] = this.additional[i].replaceFirst("^[+][+]", "--").replaceFirst("^[+]", "-");
    return this;
  }


  public List<String> options() throws Exception {
    List<String> 	result;

    result = new ArrayList<>();

    if (isVerbose()) result.add("--verbose");
    if (!getInfo().isEmpty()) result.add("--info=" + getInfo());
    if (!getDebug().isEmpty()) result.add("--debug=" + getDebug());
    if (isMsgs2stderr()) result.add("--msgs2stderr");
    if (isQuiet()) result.add("--quiet");
    if (isNoMotd()) result.add("--no-motd");
    if (isChecksum()) result.add("--checksum");
    if (isArchive()) result.add("--archive");
    if (isRecursive()) result.add("--recursive");
    if (isRelative()) result.add("--relative");
    if (isNoImpliedDirs()) result.add("--no-implied-dirs");
    if (isBackup()) result.add("--backup");
    if (!getBackupDir().isEmpty()) result.add("--backup-dir=" + getBackupDir());
    if (!getSuffix().isEmpty()) result.add("--suffix=" + getSuffix());
    if (isUpdate()) result.add("--update");
    if (isInplace()) result.add("--inplace");
    if (isAppend()) result.add("--append");
    if (isAppendVerify()) result.add("--append-verify");
    if (isDirs()) result.add("--dirs");
    if (isLinks()) result.add("--links");
    if (isCopyLinks()) result.add("--copy-links");
    if (isCopyUnsafeLinks()) result.add("--copy-unsafe-links");
    if (isSafeLinks()) result.add("--safe-links");
    if (isMungeLinks()) result.add("--munge-links");
    if (isCopyDirlinks()) result.add("--copy-dirlinks");
    if (isKeepDirlinks()) result.add("--keep-dirlinks");
    if (isHardLinks()) result.add("--hard-links");
    if (isPerms()) result.add("--perms");
    if (isExecutability()) result.add("--executability");
    if (!getChmod().isEmpty()) result.add("--chmod=" + getChmod());
    if (isAcls()) result.add("--acls");
    if (isXattrs()) result.add("--xattrs");
    if (isOwner()) result.add("--owner");
    if (isGroup()) result.add("--group");
    if (isDevices()) result.add("--devices");
    if (isSpecials()) result.add("--specials");
    if (isTimes()) result.add("--times");
    if (isOmitDirTimes()) result.add("--omit-dir-times");
    if (isOmitLinkTimes()) result.add("--omit-link-times");
    if (isSuper_()) result.add("--super");
    if (isFakeSuper()) result.add("--fake-super");
    if (isSparse()) result.add("--sparse");
    if (isPreallocate()) result.add("--preallocate");
    if (isDryRun()) result.add("--dry-run");
    if (isWholeFile()) result.add("--whole-file");
    if (isOneFileSystem()) result.add("--one-file-system");
    if (!getBlockSize().isEmpty()) result.add("--block-size=" + getBlockSize());
    if (!getRsh().isEmpty()) {
      if (SystemUtil.getOsInfo().isWindows()) {
          result.add("--rsh=\"" + getRsh() + "\"");
      }
      else {
        result.add("--rsh=" + getRsh());
      }
    }
    if (!getRsyncPath().isEmpty()) result.add("--rsync-path=" + getRsyncPath());
    if (isExisting()) result.add("--existing");
    if (isIgnoreExisting()) result.add("--ignore-existing");
    if (isRemoveSourceFiles()) result.add("--remove-source-files");
    if (isDelete()) result.add("--delete");
    if (isDeleteBefore()) result.add("--delete-before");
    if (isDeleteDuring()) result.add("--delete-during");
    if (isDeleteDelay()) result.add("--delete-delay");
    if (isDeleteAfter()) result.add("--delete-after");
    if (isDeleteExcluded()) result.add("--delete-excluded");
    if (isIgnoreMissingArgs()) result.add("--ignore-missing-args");
    if (isDeleteMissingArgs()) result.add("--delete-missing-args");
    if (isIgnoreErrors()) result.add("--ignore-errors");
    if (isForce()) result.add("--force");
    if (getMaxDelete() > -1) result.add("--max-delete=" + getMaxDelete());
    if (!getMaxSize().isEmpty()) result.add("--max-size=" + getMaxSize());
    if (!getMinSize().isEmpty()) result.add("--min-size=" + getMinSize());
    if (isPartial()) result.add("--partial");
    if (!getPartialDir().isEmpty()) result.add("--partial-dir=" + getPartialDir());
    if (isDelayUpdates()) result.add("--delay-updates");
    if (isPruneEmptyDirs()) result.add("--prune-empty-dirs");
    if (isNumericIds()) result.add("--numeric-ids");
    if (!getUsermap().isEmpty()) result.add("--usermap=" + getUsermap());
    if (!getGroupmap().isEmpty()) result.add("--groupmap=" + getGroupmap());
    if (!getChown().isEmpty()) result.add("--chown=" + getChown());
    if (getTimeout() > -1) result.add("--timeout=" + getTimeout());
    if (getContimeout() > -1) result.add("--contimeout=" + getContimeout());
    if (isIgnoreTimes()) result.add("--ignore-times");
    if (!getRemoteOption().isEmpty()) result.add("--remote-option=" + getRemoteOption());
    if (isSizeOnly()) result.add("--size-only");
    if (getModifyWindow() > -1) result.add("--modify-window=" + getModifyWindow());
    if (!getTempDir().isEmpty()) result.add("--temp-dir=" + getTempDir());
    if (isFuzzy()) result.add("--fuzzy");
    for (String compareDest: getCompareDest())
      result.add("--compare-dest=" + compareDest);
    for (String copyDest: getCopyDest())
      result.add("--copy-dest=" + copyDest);
    for (String linkDest: getLinkDest())
      result.add("--link-dest=" + linkDest);
    if (isCompress()) result.add("--compress");
    if (getCompressLevel() > -1) result.add("--compress-level=" + getCompressLevel());
    if (!getSkipCompress().isEmpty()) result.add("--skip-compress=" + getSkipCompress());
    if (isCvsExclude()) result.add("--cvs-exclude");
    for (String ie: getIncludeExclude()) {
      String id = ie.substring(0, ie.indexOf('\t'));
      String s = ie.substring(ie.indexOf('\t') + 1);
      switch (id) {
	case "E":
	  result.add("--exclude=" + s);
	  break;
	case "EF":
	  result.add("--exclude-from=" + s);
	  break;
	case "I":
	  result.add("--include=" + s);
	  break;
	case "IF":
	  result.add("--include-from=" + s);
	  break;
	case "F":
	  result.add("--filter=" + s);
	  break;
	default:
	  throw new IllegalStateException("Unhandled ID for include/exclude/include-from/exclude-from/filter list: " + id);
      }
    }
    if (!getFilesFrom().isEmpty()) result.add("--files-from=" + getFilesFrom());
    if (isFrom0()) result.add("--from0");
    if (isProtectArgs()) result.add("--protect-args");
    if (!getAddress().isEmpty()) result.add("--address=" + getAddress());
    if (getPort() > -1) result.add("--port=" + getPort());
    if (!getSockopts().isEmpty()) result.add("--sockopts=" + getSockopts());
    if (isBlockingIO()) result.add("--blocking-io");
    if (isStats()) result.add("--stats");
    if (isEightBitOutput()) result.add("--8-bit-output");
    if (isHumanReadable()) result.add("--human-readable");
    if (isProgress()) result.add("--progress");
    if (isItemizeChanges()) result.add("--itemize-changes");
    if (!getOutFormat().isEmpty()) result.add("--out-format=" + getOutFormat());
    if (!getLogFile().isEmpty()) result.add("--log-file=" + getLogFile());
    if (!getLogFileFormat().isEmpty()) result.add("--log-file-format=" + getLogFileFormat());
    if (!getPasswordFile().isEmpty()) result.add("--password-file=" + getPasswordFile());
    if (isListOnly()) result.add("--list-only");
    if (!getBwlimit().isEmpty()) result.add("--bwlimit=" + getBwlimit());
    if (getOutbuf() != '\0') result.add("--outbuf=" + getOutbuf());
    if (!getWriteBatch().isEmpty()) result.add("--write-batch=" + getWriteBatch());
    if (!getOnlyWriteBatch().isEmpty()) result.add("--only-write-batch=" + getOnlyWriteBatch());
    if (!getReadBatch().isEmpty()) result.add("--read-batch=" + getReadBatch());
    if (getProtocol() > -1) result.add("--protocol=" + getProtocol());
    if (!getIconv().isEmpty()) result.add("--iconv=" + getIconv());
    if (getChecksumSeed() > -1) result.add("--checksum-seed=" + getChecksumSeed());
    if (isIpv4()) result.add("--ipv4");
    if (isIpv6()) result.add("--ipv6");
    if (isVersion()) result.add("--version");
    // generic options
    if (additional != null) {
      for (String a : additional) {
	if ((a != null) && !a.isEmpty())
	  result.add(a);
      }
    }

    return result;
  }


  public static String convertPath(String path) {
    String result;
    result = path;
    if (SystemUtil.getOsInfo().isWindows()) {
      result = result.replace("\\", "/");
      if (result.matches("^[a-zA-Z]:.*"))
        result = "/cygdrive/" + result.substring(0, 1).toLowerCase() + "/" + result.substring(2);
    }
    return result;
  }


  public List<String> commandLineArgs() throws Exception {
    List<String> 	result;
    String 		binary;

    binary = rsyncBinary();
    result = options();
    result.add(0, binary);
    if (getSources().size() == 0)
      throw new IllegalStateException("No source(s) defined!");
    result.addAll(getSources());
    if (getDestination() == null)
      throw new IllegalStateException("No destination defined!");
    result.add(getDestination());

    return result;
  }

  protected static Boolean binariesExtracted;

  public static String rsyncBinary() throws Exception {
    if (binariesExtracted == null)
      extractBinaries();
    return rsyncBinary;
  }


  protected static void extractBinaries() throws Exception {
    String	homeDir;
    String	binDir;
    File	dir;
    rsyncBinary  = "/usr/bin/rsync";
    if (SystemUtil.getOsInfo().isLinux() || SystemUtil.getOsInfo().isMac()) {
      if (!new File(rsyncBinary).exists())
        throw new IllegalStateException("rsync not installed (" + rsyncBinary + ")?");
    }
    else if (SystemUtil.getOsInfo().isWindows()) {
      homeDir = homeDir();
      binDir  = homeDir + File.separator + "bin";
      if (!new File(binDir + File.separator + "rsync.exe").exists()) {
        DefaultSystemLog.getLog().info("Setting up rsync4j environment in '" + homeDir + "'...");
        dir = new File(binDir);
        if (!dir.exists()) {
          DefaultSystemLog.getLog().info("Creating directory: " + dir);
          if (!dir.mkdirs())
            throw new IllegalStateException("Failed to create directory: " + dir);
        }
        DefaultSystemLog.getLog().info("Copy your public key pairs into: " + dir);
        URL resource = Thread.currentThread().getContextClassLoader().getResource("");
        String path = URLDecoder.decode(resource.getPath(), "UTF-8");
        //复制到当前用户目录
        File file = FileUtil.copy(path + "rsync/windows64/bin", homeDir, true);
        if (file.exists())
          rsyncBinary  = binDir + File.separator + "rsync.exe";
      }
      else {
        rsyncBinary  = binDir + File.separator + "rsync.exe";
      }
    }
    else {
      throw new IllegalStateException(
              "Unsupported operating system: "
                      + SystemUtil.OS_NAME + "/" + SystemUtil.OS_ARCH + "/" + SystemUtil.OS_VERSION);
    }
    binariesExtracted = true;
  }



  /**
   * rsync传输
   * */
  public final static String WINDOWS_HOME_DIR = "RSYNC_HOME";

  protected static String homeDir() {
    String	result;
    File	dir;

    result = System.getProperty("user.home") + File.separator + "jpom-rsync";
    if (SystemUtil.getOsInfo().isWindows()) {
      if (System.getenv(WINDOWS_HOME_DIR) != null) {
        dir = new File(System.getenv(WINDOWS_HOME_DIR));
        if (!dir.exists() || dir.isDirectory())
          result = dir.getAbsolutePath();
      }
    }
    return result;
  }

  protected void doRsync(RSync rsync) throws Exception {
    ProcessBuilder	builder;
    int m_ExitCode = 0;
    String[] cmd = rsync.commandLineArgs().toArray(new String[]{});
    builder = new ProcessBuilder();
    builder.command(cmd);
    Process process = builder.start();
    Thread threado = new Thread(new ConsoleOutputProcessReader(process, true, ""));
    threado.start();
    m_ExitCode = process.waitFor();
    //等待主线程完成
    while (threado.isAlive()) {
      try {
        synchronized (this) {
          wait(100);
        }
      }
      catch (Exception e) {
        // ignored
      }
    }
  }

  public static void main(String[] args) {

  }
}
