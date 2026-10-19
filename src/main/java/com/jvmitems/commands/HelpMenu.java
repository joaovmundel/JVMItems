package com.jvmitems.commands;

import com.jvmitems.util.ColorUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class HelpMenu {

    private static final int WIDTH = 50;

    private static final int PAGE_LINES = 14;

    private static final String ADMIN = "jvmitems.admin";
    private static final String GIVE = "jvmitems.give";

    private static final String C_BULLET = "&8";
    private static final String C_BASE = "&7";
    private static final String C_SUB = "&e";
    private static final String C_ARGS = "&b";
    private static final String C_DESC = "&7&o";

    private static final String INDENT_ARGS = "       ";
    private static final String INDENT_DESC = "     ";

    private static final String HINT_TRIGGERS_A =
            "&8Triggers: &7right_click, left_click, sneak_right_click,";
    private static final String HINT_TRIGGERS_B =
            "&8          &7sneak_left_click, hit, take_damage, block_break, projectile_hit";
    private static final String HINT_CONDITIONS =
            "&8Condicoes: &7permission, sneaking, notsneaking, healthbelow, healthabove, chance";

    private static final boolean COMPONENTS = detectComponents();

    private static final List<HelpEntry> ENTRIES = Collections.unmodifiableList(build());

    private HelpMenu() {
    }

    // ------------------------------------------------------------- catalogo

    private static List<HelpEntry> build() {
        List<HelpEntry> list = new ArrayList<>();

        list.add(new HelpEntry("Itens", "create", "<id> <material>",
                "Cria um novo item customizado", ADMIN,
                "&8O &7id&8 e o nome interno usado nos outros comandos.",
                "&8Exemplo: &7%cmd% create espada_flamejante DIAMOND_SWORD"));
        list.add(new HelpEntry("Itens", "delete", "<id>",
                "Remove o item da lista de itens criados", ADMIN,
                "&8Nao apaga as copias que ja foram entregues."));
        list.add(new HelpEntry("Itens", "list", "",
                "Lista todos os itens customizados", ADMIN));
        list.add(new HelpEntry("Itens", "info", "<id>",
                "Mostra material, atributos e powers do item", ADMIN));
        list.add(new HelpEntry("Itens", "give", "<id> [jogador] [quantidade]",
                "Entrega o item a um jogador", GIVE,
                "&8Sem informar o jogador, o item vai para voce.",
                "&8Exemplo: &7%cmd% give espada_flamejante Steve 1"));

        list.add(new HelpEntry("Aparencia", "setname", "<id> <nome>",
                "Define o titulo do item", ADMIN,
                "&8Use &7%amp%&8 seguido do codigo da cor.",
                "&8Exemplo: &7%cmd% setname espada %amp%6%amp%lEspada Flamejante"));
        list.add(new HelpEntry("Aparencia", "addlore", "<id> <linha>",
                "Adiciona uma linha a descricao do item", ADMIN,
                "&8Aceita cores com &7%amp%&8. Cada comando adiciona uma linha nova.",
                "&8Exemplo: &7%cmd% addlore espada %amp%7Forjada no Nether."));
        list.add(new HelpEntry("Aparencia", "clearlore", "<id>",
                "Apaga toda a descricao do item", ADMIN));
        list.add(new HelpEntry("Aparencia", "setmaterial", "<id> <material>",
                "Troca o material base do item", ADMIN,
                "&8As outras configuracoes do item sao mantidas."));

        list.add(new HelpEntry("Atributos", "setdamage", "<id> <valor|none>",
                "Define o dano de ataque do item", ADMIN,
                "&8Vale enquanto o item estiver na mao. &7none&8 remove o atributo."));
        list.add(new HelpEntry("Atributos", "setarmor", "<id> <valor|none>",
                "Define os pontos de armadura do item", ADMIN,
                "&8So faz efeito com o item equipado num slot de armadura."));
        list.add(new HelpEntry("Atributos", "setarmortoughness", "<id> <valor|none>",
                "Define a resistencia de armadura (toughness)", ADMIN));
        list.add(new HelpEntry("Atributos", "setunbreakable", "<id> <true|false>",
                "Torna o item inquebravel", ADMIN));
        list.add(new HelpEntry("Atributos", "enchant add", "<id> <encantamento> [nivel]",
                "Adiciona um encantamento ao item", ADMIN,
                "&8Exemplo: &7%cmd% enchant add espada_flamejante FIRE_ASPECT 2"));
        list.add(new HelpEntry("Atributos", "enchant remove", "<id> <encantamento>",
                "Remove um encantamento do item", ADMIN));

        list.add(new HelpEntry("Powers", "power list", "<id>",
                "Lista os powers do item e seus indices", ADMIN));
        list.add(new HelpEntry("Powers", "power remove", "<id> <indice>",
                "Remove um power pelo indice", ADMIN,
                "&8Remover um power desloca o indice dos seguintes."));
        list.add(new HelpEntry("Powers", "power add arrow", "<id> <trigger> <cooldown> [qtd] [velocidade] [espalhamento]",
                "Atira flechas", ADMIN, HINT_TRIGGERS_A, HINT_TRIGGERS_B,
                "&8Exemplo: &7%cmd% power add arrow arco right_click 3 3 2.0 0.2"));
        list.add(new HelpEntry("Powers", "power add fireball", "<id> <trigger> <cooldown> [qtd] [velocidade] [espalhamento]",
                "Lanca bolas de fogo", ADMIN, HINT_TRIGGERS_A, HINT_TRIGGERS_B,
                "&8Ajuste fogo/explosao com &7%cmd% power setfireball"));
        list.add(new HelpEntry("Powers", "power add snowball", "<id> <trigger> <cooldown> [qtd] [velocidade] [espalhamento]",
                "Lanca projeteis de gelo", ADMIN, HINT_TRIGGERS_A, HINT_TRIGGERS_B,
                "&8Combine com um power &7potion&8 no trigger &7projectile_hit&8 para congelar."));
        list.add(new HelpEntry("Powers", "power add potion", "<id> <trigger> <cooldown> <self|opponent> <pocao> <duracaoTicks> <amplificador>",
                "Aplica um efeito de pocao em voce ou no alvo", ADMIN, HINT_TRIGGERS_A, HINT_TRIGGERS_B,
                "&820 ticks = 1 segundo. Amplificador 0 = nivel I.",
                "&8Exemplo: &7%cmd% power add potion espada hit 0 opponent POISON 100 1"));
        list.add(new HelpEntry("Powers", "power add lifesteal", "<id> <trigger> <cooldown> <percentual>",
                "Cura voce com base no dano causado", ADMIN,
                "&8Percentual de 0 a 1 (&70.25&8 = 25% do dano).", HINT_TRIGGERS_A, HINT_TRIGGERS_B));
        list.add(new HelpEntry("Powers", "power add damagemultiplier", "<id> <trigger> <cooldown> <multiplicador>",
                "Multiplica o dano do golpe", ADMIN,
                "&8Use com o trigger &7hit&8. &72.0&8 = o dobro de dano."));
        list.add(new HelpEntry("Powers", "power add knockback", "<id> <trigger> <cooldown> <forca>",
                "Empurra o oponente para longe", ADMIN, HINT_TRIGGERS_A, HINT_TRIGGERS_B));
        list.add(new HelpEntry("Powers", "power add lightning", "<id> <trigger> <cooldown> <causaDano>",
                "Invoca um raio no alvo", ADMIN,
                "&7causaDano&8: &7true&8 machuca, &7false&8 e so o efeito visual."));
        list.add(new HelpEntry("Powers", "power add heal", "<id> <trigger> <cooldown> <quantidade>",
                "Cura o usuario do item", ADMIN,
                "&8Quantidade em pontos de vida (&72.0&8 = 1 coracao)."));
        list.add(new HelpEntry("Powers", "power add firework", "<id> <trigger> <cooldown> <potencia>",
                "Solta um fogo de artificio no local", ADMIN,
                "&8Potencia de 0 a 2."));
        list.add(new HelpEntry("Powers", "power add aoedamage", "<id> <trigger> <cooldown> <raio> <dano>",
                "Causa dano em area ao redor do alvo", ADMIN,
                "&8Nao atinge quem esta usando o item."));
        list.add(new HelpEntry("Powers", "power add command", "<id> <trigger> <cooldown> <console> <comando>",
                "Executa um comando quando o power dispara", ADMIN,
                "&7console&8: &7true&8 executa como console, &7false&8 como o jogador.",
                "&8Placeholders: &7%player%&8 e &7%opponent%",
                "&cCuidado: executa qualquer comando. Da acesso so a quem confia."));
        list.add(new HelpEntry("Powers", "power setfireball", "<id> <indice> <incendiary> <explosive>",
                "Ajusta o fogo e a explosao da bola de fogo", ADMIN));
        list.add(new HelpEntry("Powers", "power condition add", "<id> <indice> <tipo> [valor]",
                "Adiciona uma condicao ao power", ADMIN,
                "&8Todas as condicoes precisam passar para o power disparar.",
                HINT_CONDITIONS,
                "&8Exemplo: &7%cmd% power condition add espada 0 chance 0.5"));
        list.add(new HelpEntry("Powers", "power condition remove", "<id> <indice> <indiceCondicao>",
                "Remove uma condicao do power", ADMIN));

        list.add(new HelpEntry("Geral", "help", "[pagina|busca]",
                "Mostra este menu de ajuda", null,
                "&8Exemplo: &7%cmd% help powers&8 filtra so os comandos de power."));
        list.add(new HelpEntry("Geral", "reload", "",
                "Recarrega o config.yml e o items.yml do disco", ADMIN));

        return list;
    }

    public static List<String> categories() {
        Set<String> names = new LinkedHashSet<>();
        for (HelpEntry entry : ENTRIES) {
            names.add(entry.getCategory().toLowerCase(Locale.ROOT));
        }
        return new ArrayList<>(names);
    }

    // ---------------------------------------------------------------- envio

    public static void send(CommandSender sender, String label, String[] args) {
        String base = "/" + label;

        String query = null;
        int page = 1;

        if (args.length >= 1) {
            Integer parsed = parseInt(args[0]);
            if (parsed != null) {
                page = parsed.intValue();
            } else {
                query = args[0];
                if (args.length >= 2) {
                    Integer second = parseInt(args[1]);
                    if (second != null) {
                        page = second.intValue();
                    }
                }
            }
        }

        List<HelpEntry> visible = new ArrayList<>();
        for (HelpEntry entry : ENTRIES) {
            if (entry.getPermission() != null && !sender.hasPermission(entry.getPermission())) {
                continue;
            }
            if (query != null && !entry.matches(query)) {
                continue;
            }
            visible.add(entry);
        }

        if (visible.isEmpty()) {
            if (query != null) {
                sender.sendMessage(ColorUtil.colorize("&cNenhum comando encontrado para &f" + query + "&c."));
                sender.sendMessage(ColorUtil.colorize("&7Use &e" + base + " help&7 para ver a lista completa."));
            } else {
                sender.sendMessage(ColorUtil.colorize("&cVoce nao tem permissao para nenhum comando do JVMItems."));
            }
            return;
        }

        List<List<String>> rendered = new ArrayList<>(visible.size());
        for (HelpEntry entry : visible) {
            rendered.add(entryLines(base, entry));
        }

        List<int[]> pages = paginate(visible, rendered);
        if (page < 1) {
            page = 1;
        }
        if (page > pages.size()) {
            page = pages.size();
        }

        boolean rich = COMPONENTS && sender instanceof Player;
        int[] range = pages.get(page - 1);

        try {
            render(rich ? new HelpComponents((Player) sender) : new PlainRenderer(sender),
                    base, query, page, pages.size(), visible, rendered, range, rich);
        } catch (Throwable error) {
            if (rich) {
                render(new PlainRenderer(sender), base, query, page, pages.size(),
                        visible, rendered, range, false);
            }
        }
    }

    private static List<int[]> paginate(List<HelpEntry> visible, List<List<String>> rendered) {
        List<int[]> pages = new ArrayList<>();
        int index = 0;
        while (index < visible.size()) {
            int from = index;
            int lines = 0;
            String lastCategory = null;
            while (index < visible.size()) {
                int cost = rendered.get(index).size();
                if (!visible.get(index).getCategory().equals(lastCategory)) {
                    cost++;
                }
                if (lines + cost > PAGE_LINES && index > from) {
                    break;
                }
                lines += cost;
                lastCategory = visible.get(index).getCategory();
                index++;
            }
            pages.add(new int[]{from, index});
        }
        return pages;
    }

    // -------------------------------------------------------------- render

    private static void render(HelpRenderer out, String base, String query, int page, int totalPages,
                               List<HelpEntry> visible, List<List<String>> rendered, int[] range, boolean rich) {
        out.line(one(plain("&8&m" + spaces(42))));

        String title = "&6&lJVMItems &8» &7Ajuda &8(&e" + page + "&7/&e" + totalPages + "&8)";
        if (query != null) {
            title += " &8[&7busca: &f" + shorten(query, 18) + "&8]";
        }
        out.line(one(plain(title)));

        String lastCategory = null;
        for (int i = range[0]; i < range[1]; i++) {
            HelpEntry entry = visible.get(i);

            if (!entry.getCategory().equals(lastCategory)) {
                lastCategory = entry.getCategory();
                out.line(one(plain("&8&m" + spaces(4) + "&r &6&l"
                        + lastCategory.toUpperCase(Locale.ROOT) + " &8&m" + spaces(4))));
            }

            String command = base + " " + entry.getCommand();
            boolean run = entry.getArgs().isEmpty();
            String click = run ? command : command + " ";
            String hover = buildHover(base, entry, rich);

            for (String line : rendered.get(i)) {
                out.line(one(new Segment(line, hover, click, run)));
            }
        }

        out.line(navigation(base, query, page, totalPages, rich));

        if (page == 1) {
            if (rich) {
                out.line(one(plain("&8Passe o mouse nos comandos, clique para usar.")));
            } else {
                out.line(one(plain("&8Use &7" + base + " help <busca>&8 para filtrar.")));
            }
        }
    }

    private static List<String> entryLines(String base, HelpEntry entry) {
        List<String> lines = new ArrayList<>();

        String head = " " + C_BULLET + "» " + C_BASE + base + " " + C_SUB + entry.getCommand();

        if (entry.getArgs().isEmpty()) {
            lines.add(head);
        } else {
            List<String> words = words(entry.getArgs());
            int index = 0;

            StringBuilder first = new StringBuilder(head);
            int used = visibleLength(head);
            boolean any = false;
            while (index < words.size()) {
                String word = words.get(index);
                if (used + 1 + word.length() > WIDTH) {
                    break;
                }
                first.append(' ');
                if (!any) {
                    first.append(C_ARGS);
                    any = true;
                }
                first.append(word);
                used += 1 + word.length();
                index++;
            }
            lines.add(first.toString());

            while (index < words.size()) {
                StringBuilder line = new StringBuilder(INDENT_ARGS).append(C_ARGS);
                int length = INDENT_ARGS.length();
                while (index < words.size()) {
                    String word = words.get(index);
                    int cost = length == INDENT_ARGS.length() ? word.length() : 1 + word.length();
                    if (length + cost > WIDTH && length > INDENT_ARGS.length()) {
                        break;
                    }
                    if (length > INDENT_ARGS.length()) {
                        line.append(' ');
                    }
                    line.append(word);
                    length += cost;
                    index++;
                }
                lines.add(line.toString());
            }
        }

        for (String line : wrap(entry.getDescription(), WIDTH - INDENT_DESC.length())) {
            lines.add(INDENT_DESC + C_DESC + line);
        }

        return lines;
    }

    private static String buildHover(String base, HelpEntry entry, boolean rich) {
        StringBuilder hover = new StringBuilder();
        hover.append("&6").append(base).append(" &e").append(entry.getCommand());
        if (!entry.getArgs().isEmpty()) {
            hover.append("\n&b").append(entry.getArgs());
        }
        hover.append("\n\n&f").append(entry.getDescription());

        for (String hint : entry.getHints()) {
            hover.append("\n").append(hint.replace("%cmd%", base));
        }

        hover.append("\n");
        if (entry.getPermission() != null) {
            hover.append("\n&8Permissao: &7").append(entry.getPermission());
        }
        if (rich) {
            hover.append("\n&a").append(entry.getArgs().isEmpty()
                    ? "Clique para executar"
                    : "Clique para preencher no chat");
        }
        return hover.toString();
    }

    private static List<Segment> navigation(String base, String query, int page, int totalPages, boolean rich) {
        List<Segment> segments = new ArrayList<>();
        segments.add(plain("&8&m" + spaces(6) + "&r "));

        if (page > 1) {
            segments.add(new Segment("&8[&a« Anterior&8]",
                    rich ? "&7Ir para a pagina &e" + (page - 1) : null,
                    navCommand(base, query, page - 1), true));
        } else {
            segments.add(plain("&8[« Anterior]"));
        }

        segments.add(plain(" &7" + page + "&8/&7" + totalPages + " "));

        if (page < totalPages) {
            segments.add(new Segment("&8[&aProxima »&8]",
                    rich ? "&7Ir para a pagina &e" + (page + 1) : null,
                    navCommand(base, query, page + 1), true));
        } else {
            segments.add(plain("&8[Proxima »]"));
        }

        segments.add(plain(" &8&m" + spaces(6)));
        return segments;
    }

    private static String navCommand(String base, String query, int page) {
        return base + " help " + (query == null ? "" : query + " ") + page;
    }

    // ------------------------------------------------------------- helpers

    private static List<String> words(String text) {
        List<String> words = new ArrayList<>();
        for (String word : text.split(" ")) {
            if (!word.isEmpty()) {
                words.add(word);
            }
        }
        return words;
    }

    private static List<String> wrap(String text, int width) {
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (String word : words(text)) {
            if (line.length() > 0 && line.length() + 1 + word.length() > width) {
                lines.add(line.toString());
                line.setLength(0);
            }
            if (line.length() > 0) {
                line.append(' ');
            }
            line.append(word);
        }
        if (line.length() > 0) {
            lines.add(line.toString());
        }
        return lines;
    }

    private static String shorten(String text, int max) {
        return text.length() <= max ? text : text.substring(0, max - 1) + "~";
    }

    private static int visibleLength(String legacy) {
        int length = 0;
        for (int i = 0; i < legacy.length(); i++) {
            if (legacy.charAt(i) == '&' && i + 1 < legacy.length() && isColorCode(legacy.charAt(i + 1))) {
                i++;
                continue;
            }
            length++;
        }
        return length;
    }

    private static boolean isColorCode(char c) {
        return "0123456789abcdefklmnorxABCDEFKLMNORX".indexOf(c) >= 0;
    }

    private static Integer parseInt(String value) {
        try {
            return Integer.valueOf(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String spaces(int amount) {
        StringBuilder builder = new StringBuilder(amount);
        for (int i = 0; i < amount; i++) {
            builder.append(' ');
        }
        return builder.toString();
    }

    private static Segment plain(String text) {
        return new Segment(text, null, null, false);
    }

    private static List<Segment> one(Segment segment) {
        return Collections.singletonList(segment);
    }

    static String color(String legacy) {
        return ColorUtil.colorize(legacy).replace("%amp%", "&");
    }

    private static boolean detectComponents() {
        try {
            Class.forName("net.md_5.bungee.api.chat.TextComponent");
            return true;
        } catch (Throwable error) {
            return false;
        }
    }

    // ---------------------------------------------------------------- tipos

    static final class Segment {

        final String text;
        final String hover;
        final String command;
        final boolean run;

        Segment(String text, String hover, String command, boolean run) {
            this.text = text;
            this.hover = hover;
            this.command = command;
            this.run = run;
        }
    }

    interface HelpRenderer {

        void line(List<Segment> segments);
    }

    private static final class PlainRenderer implements HelpRenderer {

        private final CommandSender sender;

        PlainRenderer(CommandSender sender) {
            this.sender = sender;
        }

        @Override
        public void line(List<Segment> segments) {
            StringBuilder builder = new StringBuilder();
            for (Segment segment : segments) {
                builder.append(segment.text);
            }
            sender.sendMessage(color(builder.toString()));
        }
    }
}
