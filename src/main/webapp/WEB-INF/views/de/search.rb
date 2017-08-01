module Precious
  module Views
    class Search < Layout
      attr_reader :content, :page, :footer, :results, :query

      def title
        "Suchergebnisse für " + @query
      end

      def has_results
        !@results.empty?
      end

      def no_results
        @results.empty?
      end

    end
  end
end
